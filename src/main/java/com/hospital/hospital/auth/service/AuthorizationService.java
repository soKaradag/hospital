package com.hospital.hospital.auth.service;

import com.hospital.hospital.auth.model.Role;

public interface AuthorizationService {

	// Mevcut oturum sahibinin verilen rollerden en az birine sahip olup olmadığını kontrol eder.
	void requireAnyRole(Role... roles);
}
