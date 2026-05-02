package com.hospital.hospital.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital.auth.annotation.RequireAuthentication;
import com.hospital.hospital.auth.dto.CurrentUserResponse;
import com.hospital.hospital.auth.service.AuthService;
import com.hospital.hospital.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/internal/auth")
public class InternalAuthController {

	private final AuthService authService;

	public InternalAuthController(AuthService authService) {
		this.authService = authService;
	}

	@GetMapping("/introspect")
	@RequireAuthentication
	public ApiResponse<CurrentUserResponse> introspect() {
		return ApiResponse.success("Authenticated user introspected successfully", authService.introspect());
	}
}
