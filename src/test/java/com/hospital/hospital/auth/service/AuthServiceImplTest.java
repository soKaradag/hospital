package com.hospital.hospital.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.dto.AuthTokenResponse;
import com.hospital.hospital.auth.dto.LoginRequest;
import com.hospital.hospital.auth.dto.RefreshTokenRequest;
import com.hospital.hospital.auth.model.RefreshToken;
import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.auth.model.UserStatus;
import com.hospital.hospital.auth.repository.RefreshTokenRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.token.AuthTokenPair;
import com.hospital.hospital.auth.token.TokenPrincipal;
import com.hospital.hospital.common.exception.UnauthorizedException;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private RefreshTokenRepository refreshTokenRepository;

	@Mock
	private com.hospital.hospital.auth.token.JwtTokenService jwtTokenService;

	@Mock
	private PasswordHashService passwordHashService;

	@Mock
	private CurrentUserContext currentUserContext;

	@Mock
	private PermissionResolutionService permissionResolutionService;

	@InjectMocks
	private AuthServiceImpl authService;

	@Test
	void loginShouldReturnTokenPairAndPersistRefreshTokenHash() {
		User user = new User("doctor1", "stored-hash", Role.DOCTOR);
		user.setId(UUID.randomUUID());

		LoginRequest request = new LoginRequest();
		request.setUsername("doctor1");
		request.setPassword("secret");

		when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(user));
		when(passwordHashService.matches("secret", "stored-hash")).thenReturn(true);
		when(permissionResolutionService.resolveRoleCodes(user.getId())).thenReturn(java.util.List.of(Role.DOCTOR.name()));
		when(jwtTokenService.generateTokenPair(user)).thenReturn(new AuthTokenPair("access-token", "refresh-token"));
		when(jwtTokenService.parseRefreshToken("refresh-token")).thenReturn(new TokenPrincipal(
				user.getId(),
				user.getUsername(),
				user.getRole(),
				com.hospital.hospital.auth.token.TokenType.REFRESH,
				Instant.parse("2026-03-27T10:00:00Z"),
				Instant.parse("2026-04-27T10:00:00Z")));
		when(jwtTokenService.hashToken("refresh-token")).thenReturn("hashed-refresh-token");

		AuthTokenResponse response = authService.login(request);

		assertNotNull(response);
		assertEquals("access-token", response.getAccessToken());
		assertEquals("refresh-token", response.getRefreshToken());

		ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
		verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
		assertEquals("hashed-refresh-token", refreshTokenCaptor.getValue().getTokenHash());
		assertEquals(user, refreshTokenCaptor.getValue().getUser());
	}

	@Test
	void loginShouldRejectInactiveUsers() {
		User user = new User("doctor1", "stored-hash", Role.DOCTOR);
		user.setId(UUID.randomUUID());
		user.setStatus(UserStatus.LOCKED);

		LoginRequest request = new LoginRequest();
		request.setUsername("doctor1");
		request.setPassword("secret");

		when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(user));
		when(passwordHashService.matches("secret", "stored-hash")).thenReturn(true);

		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));

		assertEquals("User account is not active", exception.getMessage());
	}

	@Test
	void loginShouldRejectUsersWithoutAssignedRole() {
		User user = new User("doctor1", "stored-hash", Role.DOCTOR);
		user.setId(UUID.randomUUID());

		LoginRequest request = new LoginRequest();
		request.setUsername("doctor1");
		request.setPassword("secret");

		when(userRepository.findByUsername("doctor1")).thenReturn(Optional.of(user));
		when(passwordHashService.matches("secret", "stored-hash")).thenReturn(true);
		when(permissionResolutionService.resolveRoleCodes(user.getId())).thenReturn(java.util.List.of());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class, () -> authService.login(request));

		assertEquals("User account does not have an assigned role", exception.getMessage());
	}

	@Test
	void refreshShouldRevokeExistingTokenAndCreateNewPair() {
		User user = new User("doctor1", "stored-hash", Role.DOCTOR);
		user.setId(UUID.randomUUID());
		Instant issuedAt = Instant.now().minusSeconds(60);
		Instant expiresAt = Instant.now().plusSeconds(3600);
		Instant rotatedIssuedAt = Instant.now();
		Instant rotatedExpiresAt = Instant.now().plusSeconds(7200);

		RefreshToken existingToken = new RefreshToken(user, "old-hash", expiresAt);
		existingToken.setId(UUID.randomUUID());

		RefreshTokenRequest request = new RefreshTokenRequest();
		request.setRefreshToken("old-refresh-token");

		when(jwtTokenService.hashToken("old-refresh-token")).thenReturn("old-hash");
		when(refreshTokenRepository.findByTokenHash("old-hash")).thenReturn(Optional.of(existingToken));
		when(jwtTokenService.parseRefreshToken("old-refresh-token")).thenReturn(new TokenPrincipal(
				user.getId(),
				user.getUsername(),
				user.getRole(),
				com.hospital.hospital.auth.token.TokenType.REFRESH,
				issuedAt,
				expiresAt));
		when(jwtTokenService.generateTokenPair(user)).thenReturn(new AuthTokenPair("new-access", "new-refresh"));
		when(jwtTokenService.parseRefreshToken("new-refresh")).thenReturn(new TokenPrincipal(
				user.getId(),
				user.getUsername(),
				user.getRole(),
				com.hospital.hospital.auth.token.TokenType.REFRESH,
				rotatedIssuedAt,
				rotatedExpiresAt));
		when(jwtTokenService.hashToken("new-refresh")).thenReturn("new-hash");

		AuthTokenResponse response = authService.refresh(request);

		assertEquals("new-access", response.getAccessToken());
		assertEquals("new-refresh", response.getRefreshToken());
		assertNotNull(existingToken.getRevokedAt());
		verify(refreshTokenRepository).save(any(RefreshToken.class));
	}

	@Test
	void meShouldThrowWhenRequestContextIsNotAuthenticated() {
		when(currentUserContext.isAuthenticated()).thenReturn(false);

		assertThrows(UnauthorizedException.class, () -> authService.me());
	}
}
