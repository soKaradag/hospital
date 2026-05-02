package com.hospital.inventory.auth.interceptor;

import java.util.Arrays;
import java.util.Set;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.context.CurrentUserContext;
import com.hospital.inventory.auth.dto.AuthenticatedUserResponse;
import com.hospital.inventory.auth.service.AuthorizationService;
import com.hospital.inventory.auth.service.CoreAuthIntrospectionClient;
import com.hospital.inventory.auth.token.InvalidTokenException;
import com.hospital.inventory.auth.token.JwtTokenService;
import com.hospital.inventory.auth.token.TokenPrincipal;
import com.hospital.inventory.common.exception.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";

	private final JwtTokenService jwtTokenService;
	private final CurrentUserContext currentUserContext;
	private final CoreAuthIntrospectionClient coreAuthIntrospectionClient;
	private final AuthorizationService authorizationService;

	public AuthInterceptor(
			JwtTokenService jwtTokenService,
			CurrentUserContext currentUserContext,
			CoreAuthIntrospectionClient coreAuthIntrospectionClient,
			AuthorizationService authorizationService) {
		this.jwtTokenService = jwtTokenService;
		this.currentUserContext = currentUserContext;
		this.coreAuthIntrospectionClient = coreAuthIntrospectionClient;
		this.authorizationService = authorizationService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		if (!(handler instanceof HandlerMethod handlerMethod)) {
			return true;
		}

		if (!hasPermissionRequirement(handlerMethod)) {
			return true;
		}

		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		String token = extractBearerToken(authorizationHeader);
		try {
			TokenPrincipal principal = jwtTokenService.parseAccessToken(token);
			currentUserContext.setPrincipal(principal);

			AuthenticatedUserResponse authenticatedUser = coreAuthIntrospectionClient.introspect(authorizationHeader);
			if (!principal.userId().equals(authenticatedUser.getId())) {
				throw new UnauthorizedException("Introspection result does not match access token subject");
			}

			currentUserContext.setUser(authenticatedUser);
			currentUserContext.setRoles(authenticatedUser.getRoles() != null ? authenticatedUser.getRoles() : java.util.List.of());
			currentUserContext.setPermissions(filterInventoryPermissions(authenticatedUser));
			authorizeIfRequired(handlerMethod);
			return true;
		} catch (InvalidTokenException exception) {
			throw new UnauthorizedException(exception.getMessage());
		}
	}

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

	private Set<String> filterInventoryPermissions(AuthenticatedUserResponse authenticatedUser) {
		if (authenticatedUser.getPermissions() == null) {
			return Set.of();
		}
		return authenticatedUser.getPermissions().stream()
				.filter(code -> code != null && code.startsWith("inventory."))
				.collect(java.util.stream.Collectors.toSet());
	}

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
