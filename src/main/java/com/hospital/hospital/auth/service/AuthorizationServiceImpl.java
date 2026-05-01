package com.hospital.hospital.auth.service;

import java.util.Arrays;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.token.TokenPrincipal;
import com.hospital.hospital.common.exception.ForbiddenException;
import com.hospital.hospital.common.exception.UnauthorizedException;

/*
- Bu servis, request context içindeki mevcut kullanıcı rolünü kontrol eder.
- Yetki kararını interceptor veya ileride service katmanı gibi farklı yerlerden ortak şekilde kullanabilmek için ayrı tutulur.
*/
@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	private final CurrentUserContext currentUserContext;
	private final PermissionResolutionService permissionResolutionService;

	public AuthorizationServiceImpl(
			CurrentUserContext currentUserContext,
			PermissionResolutionService permissionResolutionService) {
		this.currentUserContext = currentUserContext;
		this.permissionResolutionService = permissionResolutionService;
	}

	@Override
	// Kullanıcı giriş yapmamışsa unauthorized, giriş yapmış ama uygun permission'ı yoksa forbidden döner.
	public void requireAnyPermission(String... permissionCodes) {
		if (!currentUserContext.isAuthenticated()) {
			throw new UnauthorizedException("Authenticated user not found in current request");
		}

		TokenPrincipal principal = currentUserContext.getPrincipal();
		Set<String> grantedPermissions = permissionResolutionService.resolvePermissionCodes(principal.userId());
		boolean allowed = Arrays.stream(permissionCodes).anyMatch(grantedPermissions::contains);
		if (!allowed) {
			throw new ForbiddenException("You do not have permission to access this resource");
		}
	}
}
