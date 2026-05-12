package com.hospital.hospital.accesscontrol.service;

import java.util.UUID;

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

public interface AccessControlCommandService {

	RoleDetailResponse createRole(CreateRoleRequest request);

	RoleDetailResponse updateRole(UUID id, UpdateRoleRequest request);

	RoleDetailResponse updateRolePermissions(UUID id, UpdateRolePermissionsRequest request);

	void deleteRole(UUID id);

	UserDetailResponse createUser(CreateUserRequest request);

	UserDetailResponse updateUser(UUID id, UpdateUserRequest request);

	UserDetailResponse updateUserPassword(UUID id, UpdateUserPasswordRequest request);

	UserDetailResponse updateUserRoles(UUID id, UpdateUserRolesRequest request);

	UserDetailResponse updateUserStatus(UUID id, UpdateUserStatusRequest request);
}
