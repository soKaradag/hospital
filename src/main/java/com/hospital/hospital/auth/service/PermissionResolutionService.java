package com.hospital.hospital.auth.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface PermissionResolutionService {

	List<String> resolveRoleCodes(UUID userId);

	String resolvePrimaryRoleCode(UUID userId);

	Set<String> resolvePermissionCodes(UUID userId);
}
