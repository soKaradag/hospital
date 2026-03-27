package com.hospital.hospital.auth.token;

import com.hospital.hospital.auth.model.User;

/*
 * JWT token üretimi ve doğrulama işlemlerini yöneten servistir.
 */
public interface JwtTokenService {

	String generateAccessToken(User user);

	String generateRefreshToken(User user);

	AuthTokenPair generateTokenPair(User user);

	TokenPrincipal parseAccessToken(String token);

	TokenPrincipal parseRefreshToken(String token);

	String hashToken(String token);
}
