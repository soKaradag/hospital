package com.hospital.inventory.auth.service;

import java.util.Arrays;

import org.springframework.stereotype.Service;

import com.hospital.inventory.auth.context.CurrentUserContext;
import com.hospital.inventory.common.exception.ForbiddenException;
import com.hospital.inventory.common.exception.UnauthorizedException;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

	private final CurrentUserContext currentUserContext;

	public AuthorizationServiceImpl(CurrentUserContext currentUserContext) {
		this.currentUserContext = currentUserContext;
	}

	@Override
	public void requireAnyPermission(String... permissionCodes) {
		if (!currentUserContext.isAuthenticated()) {
			throw new UnauthorizedException("Authenticated user not found in current request");
		}

		boolean allowed = Arrays.stream(permissionCodes).anyMatch(currentUserContext.getPermissions()::contains);
		if (!allowed) {
			throw new ForbiddenException("You do not have permission to access this resource");
		}
	}
}
