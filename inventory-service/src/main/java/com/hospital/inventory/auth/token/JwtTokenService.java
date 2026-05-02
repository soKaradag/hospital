package com.hospital.inventory.auth.token;

public interface JwtTokenService {

	TokenPrincipal parseAccessToken(String token);
}
