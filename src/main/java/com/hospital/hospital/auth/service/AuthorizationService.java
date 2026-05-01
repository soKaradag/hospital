package com.hospital.hospital.auth.service;

public interface AuthorizationService {

	// Mevcut oturum sahibinin verilen permission kodlarından en az birine sahip olup olmadığını kontrol eder.
	void requireAnyPermission(String... permissionCodes);
}
