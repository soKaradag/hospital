package com.hospital.hospital.auth.service;

import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.dto.AuthTokenResponse;
import com.hospital.hospital.auth.dto.CurrentUserResponse;
import com.hospital.hospital.auth.dto.LoginRequest;
import com.hospital.hospital.auth.dto.RefreshTokenRequest;
import com.hospital.hospital.auth.model.RefreshToken;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.auth.model.UserInfo;
import com.hospital.hospital.auth.repository.RefreshTokenRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.token.AuthTokenPair;
import com.hospital.hospital.auth.token.InvalidTokenException;
import com.hospital.hospital.auth.token.JwtTokenService;
import com.hospital.hospital.auth.token.TokenPrincipal;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.common.exception.UnauthorizedException;

/*
- Auth iş akışlarının uygulama katmanı burada yürütülür.
- Login, refresh, logout ve me işlemleri controller'dan ayrılarak burada toplanır.
- Refresh token DB üzerinden yönetilir; bu sayede revoke ve rotation davranışı uygulanabilir.
*/
@Service
public class AuthServiceImpl implements AuthService {

	private final UserRepository userRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenService jwtTokenService;
	private final PasswordHashService passwordHashService;
	private final CurrentUserContext currentUserContext;

	public AuthServiceImpl(
			UserRepository userRepository,
			RefreshTokenRepository refreshTokenRepository,
			JwtTokenService jwtTokenService,
			PasswordHashService passwordHashService,
			CurrentUserContext currentUserContext) {
		this.userRepository = userRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.jwtTokenService = jwtTokenService;
		this.passwordHashService = passwordHashService;
		this.currentUserContext = currentUserContext;
	}

	@Override
	@Transactional
	// Login akışı kullanıcıyı bulur, parolayı doğrular, token çifti üretir ve refresh token kaydını kalıcılaştırır.
	@Audit(action = "LOGIN", entity = "AUTH", description = "User login flow")
	public AuthTokenResponse login(LoginRequest request) {
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

		if (!passwordHashService.matches(request.getPassword(), user.getPasswordHash())) {
			throw new UnauthorizedException("Invalid username or password");
		}

		AuthTokenPair tokenPair = jwtTokenService.generateTokenPair(user);
		saveRefreshToken(user, tokenPair.refreshToken());

		return new AuthTokenResponse(tokenPair.accessToken(), tokenPair.refreshToken());
	}

	@Override
	@Transactional
	// Refresh akışı eski refresh token'ı doğrular, revoke eder ve token rotation ile yeni çift üretir.
	// Rotation tercih edilmesinin nedeni, çalınmış eski refresh tokenların tekrar kullanılmasını zorlaştırmaktır.
	@Audit(action = "REFRESH_TOKEN", entity = "AUTH", description = "Refresh token rotation flow")
	public AuthTokenResponse refresh(RefreshTokenRequest request) {
		RefreshToken refreshToken = getActiveRefreshToken(request.getRefreshToken());
		TokenPrincipal principal = parseRefreshToken(request.getRefreshToken());

		if (!refreshToken.getUser().getId().equals(principal.userId())) {
			throw new UnauthorizedException("Refresh token is invalid");
		}

		refreshToken.setRevokedAt(Instant.now());
		User user = refreshToken.getUser();
		AuthTokenPair tokenPair = jwtTokenService.generateTokenPair(user);
		saveRefreshToken(user, tokenPair.refreshToken());

		return new AuthTokenResponse(tokenPair.accessToken(), tokenPair.refreshToken());
	}

	@Override
	@Transactional
	// Logout işlemi refresh token kaydını iptal eder; böylece yeni access token üretimi engellenir.
	@Audit(action = "LOGOUT", entity = "AUTH", description = "User logout flow")
	public void logout(RefreshTokenRequest request) {
		RefreshToken refreshToken = getActiveRefreshToken(request.getRefreshToken());
		parseRefreshToken(request.getRefreshToken());
		refreshToken.setRevokedAt(Instant.now());
	}

	@Override
	@Transactional(readOnly = true)
	// Me akışı interceptor tarafından request context'e yazılmış kullanıcı bilgisi ile çalışır.
	// Veritabanından tekrar okuma yapılmasının nedeni, token içeriğine körü körüne güvenmek yerine güncel kullanıcı kaydını kullanmaktır.
	@Audit(action = "GET_CURRENT_USER", entity = "AUTH", description = "Current authenticated user lookup")
	public CurrentUserResponse me() {
		if (!currentUserContext.isAuthenticated()) {
			throw new UnauthorizedException("Authenticated user not found in current request");
		}

		TokenPrincipal principal = currentUserContext.getPrincipal();
		User user = userRepository.findById(principal.userId())
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + principal.userId()));

		UserInfo userInfo = user.getUserInfo();
		return new CurrentUserResponse(
				user.getId(),
				user.getUsername(),
				user.getRole(),
				userInfo != null ? userInfo.getFirstName() : null,
				userInfo != null ? userInfo.getLastName() : null,
				userInfo != null && userInfo.getContact() != null ? userInfo.getContact().getEmail() : null);
	}

	// Yeni üretilen refresh token'ın ham değeri istemciye dönerken, veritabanında yalnızca hash değeri saklanır.
	// ExpiresAt bilgisi token içinden tekrar okunur; böylece DB kaydı ile JWT süresi aynı kaynaktan beslenir.
	private void saveRefreshToken(User user, String rawRefreshToken) {
		TokenPrincipal principal = parseRefreshToken(rawRefreshToken);
		RefreshToken refreshToken = new RefreshToken(
				user,
				jwtTokenService.hashToken(rawRefreshToken),
				principal.expiresAt());
		refreshTokenRepository.save(refreshToken);
	}

	// Refresh token kaydını veritabanından bulur ve aktiflik kontrollerini yapar.
	// Revoked veya süresi dolmuş tokenların burada erken reddedilmesi, refresh/logout akışlarını sadeleştirir.
	private RefreshToken getActiveRefreshToken(String rawRefreshToken) {
		String tokenHash = jwtTokenService.hashToken(rawRefreshToken);
		RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(tokenHash)
				.orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

		if (refreshToken.getRevokedAt() != null) {
			throw new UnauthorizedException("Refresh token is revoked");
		}

		if (refreshToken.getExpiresAt().isBefore(Instant.now())) {
			throw new UnauthorizedException("Refresh token is expired");
		}

		return refreshToken;
	}

	// JWT servisinden gelen teknik token hatalarını auth katmanının ortak unauthorized hatasına dönüştürür.
	private TokenPrincipal parseRefreshToken(String token) {
		try {
			return jwtTokenService.parseRefreshToken(token);
		} catch (InvalidTokenException exception) {
			throw new UnauthorizedException(exception.getMessage());
		}
	}

}
