package com.hospital.hospital.auth.dto;

/*
- Login ve refresh endpoint'lerinden dönen token çiftini taşır.
- Bu projede hem access hem refresh token response body içinde döndürülür.
- İleride refresh token'ı cookie ile taşımak istenirse bu DTO sadeleştirilebilir.
*/
public class AuthTokenResponse {

	public AuthTokenResponse() {
	}

	public AuthTokenResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	// API istemcilerinin korumalı isteklere erişmek için kullandığı kısa ömürlü token.
	private String accessToken;
	// Yeni access token üretimi için kullanılan daha uzun ömürlü token.
	private String refreshToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
