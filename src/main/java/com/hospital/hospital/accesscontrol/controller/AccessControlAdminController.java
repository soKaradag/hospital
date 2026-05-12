package com.hospital.hospital.accesscontrol.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital.accesscontrol.dto.CreateRoleRequest;
import com.hospital.hospital.accesscontrol.dto.CreateUserRequest;
import com.hospital.hospital.accesscontrol.dto.PermissionCatalogItemResponse;
import com.hospital.hospital.accesscontrol.dto.RoleDetailResponse;
import com.hospital.hospital.accesscontrol.dto.RoleSummaryResponse;
import com.hospital.hospital.accesscontrol.dto.UpdateRolePermissionsRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateRoleRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserPasswordRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserRolesRequest;
import com.hospital.hospital.accesscontrol.dto.UpdateUserStatusRequest;
import com.hospital.hospital.accesscontrol.dto.UserDetailResponse;
import com.hospital.hospital.accesscontrol.dto.UserSummaryResponse;
import com.hospital.hospital.accesscontrol.service.AccessControlCommandService;
import com.hospital.hospital.accesscontrol.service.AccessControlQueryService;
import com.hospital.hospital.auth.annotation.RequirePermission;
import com.hospital.hospital.auth.model.PermissionCodes;
import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.common.dto.PageResponse;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/admin")
public class AccessControlAdminController {

	private final AccessControlQueryService accessControlQueryService;
	private final AccessControlCommandService accessControlCommandService;

	public AccessControlAdminController(
			AccessControlQueryService accessControlQueryService,
			AccessControlCommandService accessControlCommandService) {
		this.accessControlQueryService = accessControlQueryService;
		this.accessControlCommandService = accessControlCommandService;
	}

	@GetMapping("/permissions")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_PERMISSIONS_READ)
	public ApiResponse<List<PermissionCatalogItemResponse>> getPermissions() {
		return ApiResponse.success("Access control permissions retrieved successfully",
				accessControlQueryService.getPermissionCatalog());
	}

	@GetMapping("/roles")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_ROLES_READ)
	public ApiResponse<PageResponse<RoleSummaryResponse>> getRoles(@PageableDefault(size = 20) Pageable pageable) {
		return ApiResponse.success("Access control roles retrieved successfully",
				PageResponse.from(accessControlQueryService.getRoles(pageable)));
	}

	@GetMapping("/roles/{id}")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_ROLES_READ)
	public ApiResponse<RoleDetailResponse> getRoleById(@PathVariable UUID id) {
		return ApiResponse.success("Access control role retrieved successfully",
				accessControlQueryService.getRoleById(id));
	}

	@PostMapping("/roles")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_ROLES_WRITE)
	public ApiResponse<RoleDetailResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
		return ApiResponse.success("Access control role created successfully",
				accessControlCommandService.createRole(request));
	}

	@PutMapping("/roles/{id}")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_ROLES_WRITE)
	public ApiResponse<RoleDetailResponse> updateRole(@PathVariable UUID id,
			@Valid @RequestBody UpdateRoleRequest request) {
		return ApiResponse.success("Access control role updated successfully",
				accessControlCommandService.updateRole(id, request));
	}

	@PutMapping("/roles/{id}/permissions")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_ROLES_WRITE)
	public ApiResponse<RoleDetailResponse> updateRolePermissions(@PathVariable UUID id,
			@Valid @RequestBody UpdateRolePermissionsRequest request) {
		return ApiResponse.success("Access control role permissions updated successfully",
				accessControlCommandService.updateRolePermissions(id, request));
	}

	@DeleteMapping("/roles/{id}")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_ROLES_WRITE)
	public ApiResponse<Void> deleteRole(@PathVariable UUID id) {
		accessControlCommandService.deleteRole(id);
		return ApiResponse.success("Access control role deleted successfully", null);
	}

	@GetMapping("/users")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_USERS_READ)
	public ApiResponse<PageResponse<UserSummaryResponse>> getUsers(@PageableDefault(size = 20) Pageable pageable) {
		return ApiResponse.success("Access control users retrieved successfully",
				PageResponse.from(accessControlQueryService.getUsers(pageable)));
	}

	@GetMapping("/users/{id}")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_USERS_READ)
	public ApiResponse<UserDetailResponse> getUserById(@PathVariable UUID id) {
		return ApiResponse.success("Access control user retrieved successfully",
				accessControlQueryService.getUserById(id));
	}

	@PostMapping("/users")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_USERS_WRITE)
	public ApiResponse<UserDetailResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
		return ApiResponse.success("Access control user created successfully",
				accessControlCommandService.createUser(request));
	}

	@PutMapping("/users/{id}")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_USERS_WRITE)
	public ApiResponse<UserDetailResponse> updateUser(@PathVariable UUID id,
			@Valid @RequestBody UpdateUserRequest request) {
		return ApiResponse.success("Access control user updated successfully",
				accessControlCommandService.updateUser(id, request));
	}

	@PutMapping("/users/{id}/password")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_USERS_WRITE)
	public ApiResponse<UserDetailResponse> updateUserPassword(@PathVariable UUID id,
			@Valid @RequestBody UpdateUserPasswordRequest request) {
		return ApiResponse.success("Access control user password updated successfully",
				accessControlCommandService.updateUserPassword(id, request));
	}

	@PutMapping("/users/{id}/roles")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_USERS_WRITE)
	public ApiResponse<UserDetailResponse> updateUserRoles(@PathVariable UUID id,
			@Valid @RequestBody UpdateUserRolesRequest request) {
		return ApiResponse.success("Access control user roles updated successfully",
				accessControlCommandService.updateUserRoles(id, request));
	}

	@PutMapping("/users/{id}/status")
	@RequirePermission(PermissionCodes.ACCESS_CONTROL_USERS_WRITE)
	public ApiResponse<UserDetailResponse> updateUserStatus(@PathVariable UUID id,
			@Valid @RequestBody UpdateUserStatusRequest request) {
		return ApiResponse.success("Access control user status updated successfully",
				accessControlCommandService.updateUserStatus(id, request));
	}
}
