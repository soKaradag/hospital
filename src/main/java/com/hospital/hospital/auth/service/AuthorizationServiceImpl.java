package com.hospital.hospital.auth.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.hospital.hospital.auth.context.CurrentUserContext;
import com.hospital.hospital.auth.model.Role;
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

	public AuthorizationServiceImpl(CurrentUserContext currentUserContext) {
		this.currentUserContext = currentUserContext;
	}

	@Override
	// Kullanıcı giriş yapmamışsa unauthorized, giriş yapmış ama uygun rolü yoksa forbidden döner.
	public void requireAnyRole(Role... roles) {
		if (!currentUserContext.isAuthenticated()) {
			throw new UnauthorizedException("Authenticated user not found in current request");
		}

		TokenPrincipal principal = currentUserContext.getPrincipal();
		boolean allowed = Arrays.stream(roles).anyMatch(role -> role == principal.role());
		if (!allowed) {
			throw new ForbiddenException("You do not have permission to access this resource");
		}
	}
}
