package com.hospital.hospital.accesscontrol.service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.accesscontrol.dto.CreateRoleRequest;
import com.hospital.hospital.accesscontrol.dto.CreateUserRequest;
import com.hospital.hospital.accesscontrol.dto.RoleDetailResponse;
import com.hospital.hospital.accesscontrol.dto.UpdateRolePermissionsRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateRoleRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserPasswordRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserRolesRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserStatusRequest;
import com.hospital.hospital.accesscontrol.dto.UserDetailResponse;
import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.auth.model.Permission;
import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.auth.model.RoleEntity;
import com.hospital.hospital.auth.model.RolePermission;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.auth.model.UserInfo;
import com.hospital.hospital.auth.model.UserRole;
import com.hospital.hospital.auth.model.UserStatus;
import com.hospital.hospital.auth.repository.PermissionRepository;
import com.hospital.hospital.auth.repository.RoleEntityRepository;
import com.hospital.hospital.auth.repository.RolePermissionRepository;
import com.hospital.hospital.auth.repository.UserInfoRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.repository.UserRoleRepository;
import com.hospital.hospital.auth.service.PasswordHashService;
import com.hospital.hospital.common.model.Contact;
import com.hospital.hospital.common.model.Phone;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;

@Service
public class AccessControlCommandServiceImpl implements AccessControlCommandService {

	private final RoleEntityRepository roleEntityRepository;
	private final PermissionRepository permissionRepository;
	private final RolePermissionRepository rolePermissionRepository;
	private final AccessControlQueryService accessControlQueryService;
	private final UserRepository userRepository;
	private final UserInfoRepository userInfoRepository;
	private final UserRoleRepository userRoleRepository;
	private final PasswordHashService passwordHashService;

	public AccessControlCommandServiceImpl(
			RoleEntityRepository roleEntityRepository,
			PermissionRepository permissionRepository,
			RolePermissionRepository rolePermissionRepository,
			AccessControlQueryService accessControlQueryService,
			UserRepository userRepository,
			UserInfoRepository userInfoRepository,
			UserRoleRepository userRoleRepository,
			PasswordHashService passwordHashService) {
		this.roleEntityRepository = roleEntityRepository;
		this.permissionRepository = permissionRepository;
		this.rolePermissionRepository = rolePermissionRepository;
		this.accessControlQueryService = accessControlQueryService;
		this.userRepository = userRepository;
		this.userInfoRepository = userInfoRepository;
		this.userRoleRepository = userRoleRepository;
		this.passwordHashService = passwordHashService;
	}

	@Override
	@Transactional
	@Audit(action = "CREATE_ACCESS_CONTROL_ROLE", entity = "ACCESS_CONTROL_ROLE", description = "Access control role creation")
	public RoleDetailResponse createRole(CreateRoleRequest request) {
		String roleCode = request.getCode().trim();
		if (roleEntityRepository.existsByCode(roleCode)) {
			throw new DuplicateResourceException("Role code already exists: " + roleCode);
		}

		RoleEntity role = new RoleEntity(roleCode, request.getName().trim(), request.getDescription(), false);
		RoleEntity savedRole = roleEntityRepository.save(role);
		return accessControlQueryService.getRoleById(savedRole.getId());
	}

	@Override
	@Transactional
	@Audit(action = "UPDATE_ACCESS_CONTROL_ROLE", entity = "ACCESS_CONTROL_ROLE", description = "Access control role update")
	public RoleDetailResponse updateRole(UUID id, UpdateRoleRequest request) {
		RoleEntity role = roleEntityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
		String roleCode = request.getCode().trim();

		if (roleEntityRepository.existsByCodeAndIdNot(roleCode, id)) {
			throw new DuplicateResourceException("Role code already exists: " + roleCode);
		}

		if (role.isSystemRole() && !role.getCode().equals(roleCode)) {
			throw new BusinessRuleViolationException("System role code cannot be changed");
		}

		role.setCode(roleCode);
		role.setName(request.getName().trim());
		role.setDescription(request.getDescription());
		return accessControlQueryService.getRoleById(role.getId());
	}

	@Override
	@Transactional
	@Audit(action = "UPDATE_ACCESS_CONTROL_ROLE_PERMISSIONS", entity = "ACCESS_CONTROL_ROLE", description = "Access control role permissions update")
	public RoleDetailResponse updateRolePermissions(UUID id, UpdateRolePermissionsRequest request) {
		RoleEntity role = roleEntityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
		Set<String> requestedCodes = normalizePermissionCodes(request.getPermissionCodes());

		if (role.isSystemRole() && requestedCodes.isEmpty()) {
			throw new BusinessRuleViolationException("System role permissions cannot be empty");
		}

		List<Permission> permissions = permissionRepository.findAllByCodeInOrderByCodeAsc(requestedCodes);
		if (permissions.size() != requestedCodes.size()) {
			Set<String> foundCodes = permissions.stream().map(Permission::getCode).collect(java.util.stream.Collectors.toSet());
			String missingCode = requestedCodes.stream().filter(code -> !foundCodes.contains(code)).findFirst().orElse("unknown");
			throw new BusinessRuleViolationException("Unknown permission code: " + missingCode);
		}

		Set<String> existingCodes = new LinkedHashSet<>(rolePermissionRepository.findPermissionCodesByRoleId(role.getId()));
		for (Permission permission : permissions) {
			if (existingCodes.contains(permission.getCode())) {
				continue;
			}
			rolePermissionRepository.save(new RolePermission(role, permission));
		}

		return accessControlQueryService.getRoleById(role.getId());
	}

	@Override
	@Transactional
	public void deleteRole(UUID id) {
		RoleEntity role = roleEntityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
		if (role.isSystemRole()) {
			throw new BusinessRuleViolationException("System roles cannot be deleted");
		}
		if (userRoleRepository.countByRole_Id(id) > 0) {
			throw new BusinessRuleViolationException("Role cannot be deleted while assigned to users");
		}

		rolePermissionRepository.deleteByRole_Id(id);
		roleEntityRepository.delete(role);
	}

	@Override
	@Transactional
	@Audit(action = "CREATE_ACCESS_CONTROL_USER", entity = "ACCESS_CONTROL_USER", description = "Access control user creation")
	public UserDetailResponse createUser(CreateUserRequest request) {
		String username = request.getUsername().trim();
		if (userRepository.existsByUsername(username)) {
			throw new DuplicateResourceException("Username already exists: " + username);
		}

		List<RoleEntity> roles = resolveAssignedRoles(request.getRoleIds(), request.getPrimaryRoleId());
		Role legacyRole = resolveLegacyMirrorRole(roles, request.getPrimaryRoleId());

		User user = new User(username, passwordHashService.hash(request.getPassword()), legacyRole);
		user.setStatus(UserStatus.ACTIVE);
		User savedUser = userRepository.save(user);

		UserInfo userInfo = userInfoRepository.save(new UserInfo(
				savedUser,
				request.getFirstName().trim(),
				request.getLastName().trim(),
				buildContact(request.getPhoneCountryCode(), request.getPhoneNumber(), request.getEmail())));

		savedUser.setUserInfo(userInfo);
		replaceUserRoles(savedUser, roles, request.getPrimaryRoleId());
		return accessControlQueryService.getUserById(savedUser.getId());
	}

	@Override
	@Transactional
	@Audit(action = "UPDATE_ACCESS_CONTROL_USER", entity = "ACCESS_CONTROL_USER", description = "Access control user profile update")
	public UserDetailResponse updateUser(UUID id, UpdateUserRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
		UserInfo userInfo = userInfoRepository.findByUser_Id(id)
				.orElseGet(() -> new UserInfo(user, request.getFirstName().trim(), request.getLastName().trim(), null));

		userInfo.setFirstName(request.getFirstName().trim());
		userInfo.setLastName(request.getLastName().trim());
		userInfo.setContact(buildContact(request.getPhoneCountryCode(), request.getPhoneNumber(), request.getEmail()));
		userInfoRepository.save(userInfo);
		return accessControlQueryService.getUserById(id);
	}

	@Override
	@Transactional
	@Audit(action = "UPDATE_ACCESS_CONTROL_USER_PASSWORD", entity = "ACCESS_CONTROL_USER", description = "Access control user password reset")
	public UserDetailResponse updateUserPassword(UUID id, UpdateUserPasswordRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
		user.setPasswordHash(passwordHashService.hash(request.getNewPassword()));
		return accessControlQueryService.getUserById(id);
	}

	@Override
	@Transactional
	@Audit(action = "UPDATE_ACCESS_CONTROL_USER_ROLES", entity = "ACCESS_CONTROL_USER", description = "Access control user roles update")
	public UserDetailResponse updateUserRoles(UUID id, UpdateUserRolesRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
		List<RoleEntity> roles = resolveAssignedRoles(request.getRoleIds(), request.getPrimaryRoleId());
		user.setRole(resolveLegacyMirrorRole(roles, request.getPrimaryRoleId()));
		replaceUserRoles(user, roles, request.getPrimaryRoleId());
		return accessControlQueryService.getUserById(id);
	}

	@Override
	@Transactional
	@Audit(action = "UPDATE_ACCESS_CONTROL_USER_STATUS", entity = "ACCESS_CONTROL_USER", description = "Access control user status update")
	public UserDetailResponse updateUserStatus(UUID id, UpdateUserStatusRequest request) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

		if (user.getStatus() == UserStatus.ACTIVE
				&& request.getStatus() != UserStatus.ACTIVE
				&& userRoleRepository.existsActiveAdminExcluding(user.getId()) == false
				&& userRoleRepository.hasRoleCode(user.getId(), Role.ADMIN.name())) {
			throw new BusinessRuleViolationException("Last active admin user cannot be deactivated");
		}

		user.setStatus(request.getStatus());
		return accessControlQueryService.getUserById(id);
	}

	private Set<String> normalizePermissionCodes(List<String> permissionCodes) {
		if (permissionCodes == null) {
			return Set.of();
		}

		return permissionCodes.stream()
				.map(code -> code == null ? null : code.trim())
				.filter(code -> code != null && !code.isBlank())
				.collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
	}

	private List<RoleEntity> resolveAssignedRoles(List<UUID> roleIds, UUID primaryRoleId) {
		Set<UUID> uniqueRoleIds = new LinkedHashSet<>(roleIds);
		if (!uniqueRoleIds.contains(primaryRoleId)) {
			throw new BusinessRuleViolationException("Primary role must be included in assigned roles");
		}

		List<RoleEntity> roles = roleEntityRepository.findAllById(uniqueRoleIds);
		if (roles.size() != uniqueRoleIds.size()) {
			throw new BusinessRuleViolationException("One or more assigned roles were not found");
		}
		return roles;
	}

	private Role resolveLegacyMirrorRole(List<RoleEntity> roles, UUID primaryRoleId) {
		RoleEntity primaryRole = roles.stream()
				.filter(role -> role.getId().equals(primaryRoleId))
				.findFirst()
				.orElseThrow(() -> new BusinessRuleViolationException("Primary role must be included in assigned roles"));

		Role mirroredPrimary = toLegacyRole(primaryRole.getCode());
		if (mirroredPrimary != null) {
			return mirroredPrimary;
		}

		return roles.stream()
				.map(role -> toLegacyRole(role.getCode()))
				.filter(java.util.Objects::nonNull)
				.findFirst()
				.orElse(Role.CUSTOM);
	}

	private Role toLegacyRole(String roleCode) {
		try {
			return Role.valueOf(roleCode);
		} catch (IllegalArgumentException exception) {
			return null;
		}
	}

	private Contact buildContact(String phoneCountryCode, String phoneNumber, String email) {
		Phone phone = null;
		if ((phoneCountryCode != null && !phoneCountryCode.isBlank())
				|| (phoneNumber != null && !phoneNumber.isBlank())) {
			phone = new Phone(trimToNull(phoneCountryCode), trimToNull(phoneNumber));
		}
		return new Contact(phone, trimToNull(email));
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private void replaceUserRoles(User user, List<RoleEntity> roles, UUID primaryRoleId) {
		userRoleRepository.deleteByUser_Id(user.getId());
		userRoleRepository.flush();
		for (RoleEntity role : roles) {
			userRoleRepository.save(new UserRole(user, role, role.getId().equals(primaryRoleId)));
		}
	}
}
