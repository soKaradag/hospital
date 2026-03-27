package com.hospital.hospital.auth.interceptor;

import java.util.Arrays;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.core.annotation.AnnotatedElementUtils;
import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.annotation.RequireRole;
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

		// Bu aşamada auth yalnızca @RequireRole ile işaretlenmiş endpoint'lerde zorunlu tutulur.
		// Böylece Faz 1'den gelen ve henüz role koruması eklenmemiş endpoint'ler çalışmaya devam eder.
		if (!hasRoleRequirement(handlerMethod)) {
			return true;
		}

		String token = extractBearerToken(request.getHeader(AUTHORIZATION_HEADER));
		try {
			TokenPrincipal principal = jwtTokenService.parseAccessToken(token);
			currentUserContext.setPrincipal(principal);
			authorizeIfRequired(handlerMethod);
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
		RequireRole requireRole = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequireRole.class);
		if (requireRole == null) {
			requireRole = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequireRole.class);
		}
		if (requireRole != null && requireRole.value().length > 0) {
			authorizationService.requireAnyRole(Arrays.copyOf(requireRole.value(), requireRole.value().length));
		}
	}

	private boolean hasRoleRequirement(HandlerMethod handlerMethod) {
		return AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getMethod(), RequireRole.class) != null
				|| AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RequireRole.class) != null;
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
