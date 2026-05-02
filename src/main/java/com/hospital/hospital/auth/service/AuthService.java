package com.hospital.hospital.auth.service;

import com.hospital.hospital.auth.dto.AuthTokenResponse;
import com.hospital.hospital.auth.dto.CurrentUserResponse;
import com.hospital.hospital.auth.dto.LoginRequest;
import com.hospital.hospital.auth.dto.RefreshTokenRequest;

public interface AuthService {

	// Kullanıcı adı ve şifre ile giriş yapar, başarılıysa token çifti döner.
	AuthTokenResponse login(LoginRequest request);

	// Geçerli refresh token ile yeni access ve refresh token üretir.
	AuthTokenResponse refresh(RefreshTokenRequest request);

	// Refresh token kaydını iptal ederek logout işlemini tamamlar.
	void logout(RefreshTokenRequest request);

	// Request context içine yazılmış access token sahibini kullanarak mevcut kullanıcı bilgisini döner.
	CurrentUserResponse me();

	CurrentUserResponse introspect();
}
