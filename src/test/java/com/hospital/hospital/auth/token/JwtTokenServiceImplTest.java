package com.hospital.hospital.auth.token;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.auth.model.User;

class JwtTokenServiceImplTest {

	@Test
	void generateRefreshTokenShouldProduceDistinctTokensForBackToBackLogins() {
		JwtTokenServiceImpl jwtTokenService = new JwtTokenServiceImpl(
				"phase-4-test-secret-key-that-is-long-enough",
				3600,
				86400);
		User user = new User("admin", "password-hash", Role.ADMIN);
		user.setId(UUID.randomUUID());

		String firstRefreshToken = jwtTokenService.generateRefreshToken(user);
		String secondRefreshToken = jwtTokenService.generateRefreshToken(user);

		assertNotEquals(firstRefreshToken, secondRefreshToken);
		assertNotEquals(jwtTokenService.hashToken(firstRefreshToken), jwtTokenService.hashToken(secondRefreshToken));
		assertDoesNotThrow(() -> jwtTokenService.parseRefreshToken(firstRefreshToken));
		assertDoesNotThrow(() -> jwtTokenService.parseRefreshToken(secondRefreshToken));
	}
}
