package com.hospital.inventory.auth.service;

public interface AuthorizationService {

	void requireAnyPermission(String... permissionCodes);
}
