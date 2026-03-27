package com.hospital.hospital.auth.interceptor;

import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.token.InvalidTokenException;
import com.hospital.hospital.auth.token.JwtTokenService;
import com.hospital.hospital.auth.token.TokenPrincipal;
import com.hospital.hospital.common.exception.UnauthorizedException;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
- Bu interceptor, korumalı endpoint'lere gelen access token'ı request girişinde doğrular.
- Token çözümleme controller ve service katmanlarından çıkarılarak merkezi hale getirilir.
- Login ve refresh gibi public endpoint'ler burada hariç tutulur.
*/
@Component
public class AuthInterceptor implements HandlerInterceptor {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenService jwtTokenService;
	private final CurrentUserContext currentUserContext;

	public AuthInterceptor(JwtTokenService jwtTokenService, CurrentUserContext currentUserContext) {
		this.jwtTokenService = jwtTokenService;
		this.currentUserContext = currentUserContext;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}

		if (isPublicAuthEndpoint(request)) {
			return true;
		}

		String token = extractBearerToken(request.getHeader(AUTHORIZATION_HEADER));
		try {
			TokenPrincipal principal = jwtTokenService.parseAccessToken(token);
			currentUserContext.setPrincipal(principal);
			return true;
		} catch (InvalidTokenException exception) {
			throw new UnauthorizedException(exception.getMessage());
		}
	}

	// Faz 2'nin bu aşamasında login ve refresh endpoint'leri anonim erişime açıktır.
	private boolean isPublicAuthEndpoint(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return "/api/auth/login".equals(requestUri) || "/api/auth/refresh".equals(requestUri);
	}

	// Bearer token çıkarma işlemi request girişinde tek yerde yapılır; böylece service katmanı sadeleşir.
	private String extractBearerToken(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new UnauthorizedException("Authorization header is required");
		}

		if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
			throw new UnauthorizedException("Authorization header must use Bearer token");
		}

		String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
		if (token.isBlank()) {
			throw new UnauthorizedException("Bearer token must not be blank");
		}
		return token;
	}
}
