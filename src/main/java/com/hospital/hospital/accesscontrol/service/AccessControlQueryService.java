package com.hospital.hospital.accesscontrol.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.accesscontrol.dto.PermissionCatalogItemResponse;
import com.hospital.hospital.accesscontrol.dto.RoleDetailResponse;
import com.hospital.hospital.accesscontrol.dto.RoleSummaryResponse;
import com.hospital.hospital.accesscontrol.dto.UserDetailResponse;
import com.hospital.hospital.accesscontrol.dto.UserSummaryResponse;

public interface AccessControlQueryService {

	List<PermissionCatalogItemResponse> getPermissionCatalog();

	Page<RoleSummaryResponse> getRoles(Pageable pageable);

	RoleDetailResponse getRoleById(UUID id);

	Page<UserSummaryResponse> getUsers(Pageable pageable);

	UserDetailResponse getUserById(UUID id);
}
