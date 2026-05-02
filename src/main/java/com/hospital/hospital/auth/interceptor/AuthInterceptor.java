package com.hospital.hospital.auth.interceptor;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import com.hospital.hospital.auth.annotation.RequireAuthentication;
import com.hospital.hospital.auth.annotation.RequirePermission;
import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.service.AuthorizationService;
import com.hospital.hospital.auth.token.InvalidTokenException;
import com.hospital.hospital.auth.token.JwtTokenService;
import com.hospital.hospital.auth.token.TokenPrincipal;
import com.hospital.hospital.common.exception.UnauthorizedException;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
- Bu interceptor, korumalı endpoint'lere gelen access token'ı request girişinde doğrular.
- Token çözümleme controller ve service katmanlarından çıkarılarak merkezi hale getirilir.
- Login ve refresh gibi public endpoint'ler burada hariç tutulur.
*/
@Component
@ConditionalOnBean(JwtTokenService.class)
public class AuthInterceptor implements HandlerInterceptor {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenService jwtTokenService;
	private final CurrentUserContext currentUserContext;
	private final AuthorizationService authorizationService;

	public AuthInterceptor(
			JwtTokenService jwtTokenService,
			CurrentUserContext currentUserContext,
			AuthorizationService authorizationService) {
		this.jwtTokenService = jwtTokenService;
		this.currentUserContext = currentUserContext;
		this.authorizationService = authorizationService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		if (isPublicAuthEndpoint(request)) {
			return true;
		}

		boolean authenticationRequired = requiresAuthentication(handlerMethod);
		if (!authenticationRequired) {
			return true;
		}

		String token = extractBearerToken(request.getHeader(AUTHORIZATION_HEADER));
		try {
			TokenPrincipal principal = jwtTokenService.parseAccessToken(token);
			currentUserContext.setPrincipal(principal);
			currentUserContext.setRawAccessToken(token);
			if (hasPermissionRequirement(handlerMethod)) {
				authorizeIfRequired(handlerMethod);
			}
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

	// Annotation method seviyesinde aranır; bulunmazsa class seviyesindeki kural kullanılır.
	private void authorizeIfRequired(HandlerMethod handlerMethod) {
		RequirePermission requirePermission = AnnotatedElementUtils.findMergedAnnotation(
				handlerMethod.getMethod(),
				RequirePermission.class);
		if (requirePermission == null) {
			requirePermission = AnnotatedElementUtils.findMergedAnnotation(
					handlerMethod.getBeanType(),
					RequirePermission.class);
		}
		if (requirePermission != null && requirePermission.value().length > 0) {
			authorizationService.requireAnyPermission(
					Arrays.copyOf(requirePermission.value(), requirePermission.value().length));
		}
	}

	private boolean hasPermissionRequirement(HandlerMethod handlerMethod) {
		return AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequirePermission.class) != null
				|| AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequirePermission.class) != null;
	}

	private boolean hasAuthenticationRequirement(HandlerMethod handlerMethod) {
		return AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequireAuthentication.class) != null
				|| AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(),
						RequireAuthentication.class) != null;
	}

	private boolean requiresAuthentication(HandlerMethod handlerMethod) {
		return hasPermissionRequirement(handlerMethod) || hasAuthenticationRequirement(handlerMethod);
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
