package com.hospital.hospital.auth.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital.auth.dto.AuthTokenResponse;
import com.hospital.hospital.auth.dto.CurrentUserResponse;
import com.hospital.hospital.auth.dto.LoginRequest;
import com.hospital.hospital.auth.dto.RefreshTokenRequest;
import com.hospital.hospital.auth.service.AuthService;
import com.hospital.hospital.common.dto.ApiResponse;

import jakarta.validation.Valid;

/*
- Bu controller auth domain'ine ait HTTP giriş noktalarını toplar.
- İş kuralı yazmaz; gelen isteği doğrular ve service katmanına yönlendirir.
- Tüm başarılı cevaplar ortak API standardını korumak için ApiResponse ile sarılır.
- Faz 2'nin bu adımında access ve refresh token response body içinde döndürülür.
- Me endpoint'i access token'ı doğrudan okumaz; bu iş artık merkezi auth interceptor tarafından yapılır.
*/
@Validated
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	// Login endpoint'i kullanıcı adı ve şifreyi alır, doğrulama başarılıysa access ve refresh token döner.
	@PostMapping("/login")
	public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return ApiResponse.success("Login successful", authService.login(request));
	}

	// Refresh endpoint'i geçerli refresh token ile yeni token çifti üretir.
	@PostMapping("/refresh")
	public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		return ApiResponse.success("Token refreshed successfully", authService.refresh(request));
	}

	// Logout endpoint'i refresh token kaydını iptal ederek oturum yenilemeyi durdurur.
	@PostMapping("/logout")
	public ApiResponse<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
		authService.logout(request);
		return ApiResponse.success("Logout successful", null);
	}

	// Me endpoint'i request context'e yazılmış mevcut oturum sahibini döner.
	@GetMapping("/me")
	public ApiResponse<CurrentUserResponse> me() {
		return ApiResponse.success("Current user retrieved successfully", authService.me());
	}
}
