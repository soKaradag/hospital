package com.hospital.hospital.auth.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.auth.repository.PermissionRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.repository.UserRoleRepository;

@Service
public class PermissionResolutionServiceImpl implements PermissionResolutionService {

	private final UserRoleRepository userRoleRepository;
	private final UserRepository userRepository;
	private final PermissionRepository permissionRepository;

	public PermissionResolutionServiceImpl(
			UserRoleRepository userRoleRepository,
			UserRepository userRepository,
			PermissionRepository permissionRepository) {
		this.userRoleRepository = userRoleRepository;
		this.userRepository = userRepository;
		this.permissionRepository = permissionRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> resolveRoleCodes(UUID userId) {
		List<String> roleCodes = userRoleRepository.findRoleCodesByUserId(userId);
		if (!roleCodes.isEmpty()) {
			return roleCodes;
		}

		return userRepository.findById(userId)
				.map(user -> List.of(user.getRole().name()))
				.orElseGet(List::of);
	}

	@Override
	@Transactional(readOnly = true)
	public String resolvePrimaryRoleCode(UUID userId) {
		return userRoleRepository.findPrimaryRoleCodeByUserId(userId)
				.orElseGet(() -> resolveRoleCodes(userId).stream().findFirst().orElse(null));
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> resolvePermissionCodes(UUID userId) {
		List<String> roleCodes = resolveRoleCodes(userId);
		if (roleCodes.isEmpty()) {
			return Set.of();
		}

		return new LinkedHashSet<>(permissionRepository.findPermissionCodesByRoleCodes(roleCodes));
	}
}
