package com.hospital.hospital.auth.dto;

import jakarta.validation.constraints.NotBlank;

// Refresh ve logout endpoint'lerinde ortak olarak kullanılan refresh token request'idir.
public class RefreshTokenRequest {

	@NotBlank(message = "refreshToken must not be blank")
	private String refreshToken;

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
