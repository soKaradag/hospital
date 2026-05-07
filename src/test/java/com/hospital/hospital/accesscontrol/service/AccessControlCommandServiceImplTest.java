package com.hospital.hospital.accesscontrol.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.accesscontrol.dto.CreateRoleRequest;
import com.hospital.hospital.accesscontrol.dto.CreateUserRequest;
import com.hospital.hospital.accesscontrol.dto.RoleDetailResponse;
import com.hospital.hospital.accesscontrol.dto.UpdateRolePermissionsRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateRoleRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserStatusRequest;
import com.hospital.hospital.auth.model.RoleEntity;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.auth.model.UserStatus;
import com.hospital.hospital.auth.repository.PermissionRepository;
import com.hospital.hospital.auth.repository.RoleEntityRepository;
import com.hospital.hospital.auth.repository.RolePermissionRepository;
import com.hospital.hospital.auth.repository.UserInfoRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.repository.UserRoleRepository;
import com.hospital.hospital.auth.service.PasswordHashService;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.DuplicateResourceException;

@ExtendWith(MockitoExtension.class)
class AccessControlCommandServiceImplTest {

	@Mock
	private RoleEntityRepository roleEntityRepository;

	@Mock
	private PermissionRepository permissionRepository;

	@Mock
	private RolePermissionRepository rolePermissionRepository;

	@Mock
	private AccessControlQueryService accessControlQueryService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserInfoRepository userInfoRepository;

	@Mock
	private UserRoleRepository userRoleRepository;

	@Mock
	private PasswordHashService passwordHashService;

	@InjectMocks
	private AccessControlCommandServiceImpl accessControlCommandService;

	@Test
	void createRoleShouldRejectDuplicateCode() {
		CreateRoleRequest request = new CreateRoleRequest();
		request.setCode("DOCTOR");
		request.setName("Doctor");

		when(roleEntityRepository.existsByCode("DOCTOR")).thenReturn(true);

		DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
				() -> accessControlCommandService.createRole(request));

		assertEquals("Role code already exists: DOCTOR", exception.getMessage());
	}

	@Test
	void updateRoleShouldRejectSystemRoleCodeChange() {
		UUID roleId = UUID.randomUUID();
		RoleEntity role = new RoleEntity("ADMIN", "Admin", "System admin", true);
		role.setId(roleId);

		UpdateRoleRequest request = new UpdateRoleRequest();
		request.setCode("SUPER_ADMIN");
		request.setName("Admin");

		when(roleEntityRepository.findById(roleId)).thenReturn(Optional.of(role));
		when(roleEntityRepository.existsByCodeAndIdNot("SUPER_ADMIN", roleId)).thenReturn(false);

		BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
				() -> accessControlCommandService.updateRole(roleId, request));

		assertEquals("System role code cannot be changed", exception.getMessage());
	}

	@Test
	void updateRolePermissionsShouldRejectUnknownPermissionCodes() {
		UUID roleId = UUID.randomUUID();
		RoleEntity role = new RoleEntity("CUSTOM_ROLE", "Custom role", null, false);
		role.setId(roleId);

		UpdateRolePermissionsRequest request = new UpdateRolePermissionsRequest();
		request.setPermissionCodes(List.of("missing.permission"));

		when(roleEntityRepository.findById(roleId)).thenReturn(Optional.of(role));
		when(permissionRepository.findAllByCodeInOrderByCodeAsc(java.util.Set.of("missing.permission"))).thenReturn(List.of());

		BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
				() -> accessControlCommandService.updateRolePermissions(roleId, request));

		assertEquals("Unknown permission code: missing.permission", exception.getMessage());
	}

	@Test
	void createRoleShouldReturnCreatedRoleDetail() {
		UUID roleId = UUID.randomUUID();
		CreateRoleRequest request = new CreateRoleRequest();
		request.setCode("PROCUREMENT");
		request.setName("Procurement");
		request.setDescription("Procurement staff");

		RoleEntity savedRole = new RoleEntity("PROCUREMENT", "Procurement", "Procurement staff", false);
		savedRole.setId(roleId);

		when(roleEntityRepository.existsByCode("PROCUREMENT")).thenReturn(false);
		when(roleEntityRepository.save(org.mockito.ArgumentMatchers.any(RoleEntity.class))).thenReturn(savedRole);
		when(accessControlQueryService.getRoleById(roleId))
				.thenReturn(new RoleDetailResponse(roleId, "PROCUREMENT", "Procurement", "Procurement staff", false, List.of()));

		RoleDetailResponse response = accessControlCommandService.createRole(request);

		assertEquals(roleId, response.getId());
		assertEquals("PROCUREMENT", response.getCode());
	}

	@Test
	void createUserShouldRejectDuplicateUsername() {
		CreateUserRequest request = new CreateUserRequest();
		request.setUsername("doctor1");
		request.setPassword("secret123");
		request.setFirstName("Doc");
		request.setLastName("Tor");
		request.setRoleIds(List.of(UUID.randomUUID()));
		request.setPrimaryRoleId(request.getRoleIds().get(0));

		when(userRepository.existsByUsername("doctor1")).thenReturn(true);

		DuplicateResourceException exception = assertThrows(DuplicateResourceException.class,
				() -> accessControlCommandService.createUser(request));

		assertEquals("Username already exists: doctor1", exception.getMessage());
	}

	@Test
	void createUserShouldRequireAtLeastOneSystemRoleDuringTransition() {
		UUID roleId = UUID.randomUUID();
		RoleEntity customRole = new RoleEntity("PROCUREMENT_MANAGER", "Procurement", null, false);
		customRole.setId(roleId);

		CreateUserRequest request = new CreateUserRequest();
		request.setUsername("procurement1");
		request.setPassword("secret123");
		request.setFirstName("Proc");
		request.setLastName("User");
		request.setRoleIds(List.of(roleId));
		request.setPrimaryRoleId(roleId);

		when(userRepository.existsByUsername("procurement1")).thenReturn(false);
		when(roleEntityRepository.findAllById(java.util.Set.of(roleId))).thenReturn(List.of(customRole));

		BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
				() -> accessControlCommandService.createUser(request));

		assertEquals("At least one assigned system role is required during transition", exception.getMessage());
	}

	@Test
	void updateUserStatusShouldRejectDeactivatingLastActiveAdmin() {
		UUID userId = UUID.randomUUID();
		User user = new User("admin", "hash", com.hospital.hospital.auth.model.Role.ADMIN);
		user.setId(userId);
		user.setStatus(UserStatus.ACTIVE);

		UpdateUserStatusRequest request = new UpdateUserStatusRequest();
		request.setStatus(UserStatus.PASSIVE);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRoleRepository.existsActiveAdminExcluding(userId)).thenReturn(false);
		when(userRoleRepository.hasRoleCode(userId, com.hospital.hospital.auth.model.Role.ADMIN.name())).thenReturn(true);

		BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
				() -> accessControlCommandService.updateUserStatus(userId, request));

		assertEquals("Last active admin user cannot be deactivated", exception.getMessage());
	}
}
