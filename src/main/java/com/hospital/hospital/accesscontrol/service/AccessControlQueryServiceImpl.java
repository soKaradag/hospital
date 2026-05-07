package com.hospital.hospital.accesscontrol.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.accesscontrol.dto.PermissionCatalogItemResponse;
import com.hospital.hospital.accesscontrol.dto.RoleDetailResponse;
import com.hospital.hospital.accesscontrol.dto.RoleSummaryResponse;
import com.hospital.hospital.accesscontrol.dto.UserDetailResponse;
import com.hospital.hospital.accesscontrol.dto.UserSummaryResponse;
import com.hospital.hospital.auth.model.Permission;
import com.hospital.hospital.auth.model.RoleEntity;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.auth.model.UserInfo;
import com.hospital.hospital.auth.repository.PermissionRepository;
import com.hospital.hospital.auth.repository.RoleEntityRepository;
import com.hospital.hospital.auth.repository.RolePermissionRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.repository.UserRoleRepository;
import com.hospital.hospital.common.exception.ResourceNotFoundException;

@Service
public class AccessControlQueryServiceImpl implements AccessControlQueryService {

	private final PermissionRepository permissionRepository;
	private final RoleEntityRepository roleEntityRepository;
	private final RolePermissionRepository rolePermissionRepository;
	private final UserRepository userRepository;
	private final UserRoleRepository userRoleRepository;

	public AccessControlQueryServiceImpl(
			PermissionRepository permissionRepository,
			RoleEntityRepository roleEntityRepository,
			RolePermissionRepository rolePermissionRepository,
			UserRepository userRepository,
			UserRoleRepository userRoleRepository) {
		this.permissionRepository = permissionRepository;
		this.roleEntityRepository = roleEntityRepository;
		this.rolePermissionRepository = rolePermissionRepository;
		this.userRepository = userRepository;
		this.userRoleRepository = userRoleRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PermissionCatalogItemResponse> getPermissionCatalog() {
		return permissionRepository.findAllByOrderByCodeAsc().stream()
				.map(permission -> new PermissionCatalogItemResponse(
						permission.getCode(),
						permission.getName(),
						resolveGroup(permission)))
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RoleSummaryResponse> getRoles(Pageable pageable) {
		return roleEntityRepository.findAllByOrderByCodeAsc(pageable)
				.map(role -> new RoleSummaryResponse(
						role.getId(),
						role.getCode(),
						role.getName(),
						role.getDescription(),
						role.isSystemRole(),
						rolePermissionRepository.countByRole_Id(role.getId())));
	}

	@Override
	@Transactional(readOnly = true)
	public RoleDetailResponse getRoleById(UUID id) {
		RoleEntity role = roleEntityRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + id));
		return new RoleDetailResponse(
				role.getId(),
				role.getCode(),
				role.getName(),
				role.getDescription(),
				role.isSystemRole(),
				rolePermissionRepository.findPermissionCodesByRoleId(role.getId()));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<UserSummaryResponse> getUsers(Pageable pageable) {
		return userRepository.findAllByOrderByUsernameAsc(pageable)
				.map(user -> new UserSummaryResponse(
						user.getId(),
						user.getUsername(),
						buildFullName(user.getUserInfo()),
						resolveEmail(user.getUserInfo()),
						user.getStatus().name(),
						userRoleRepository.findPrimaryRoleCodeByUserId(user.getId()).orElse(null),
						userRoleRepository.countByUser_Id(user.getId()),
						userRoleRepository.findRoleCodesByUserId(user.getId())));
	}

	@Override
	@Transactional(readOnly = true)
	public UserDetailResponse getUserById(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
		UserInfo userInfo = user.getUserInfo();
		return new UserDetailResponse(
				user.getId(),
				user.getUsername(),
				userInfo != null ? userInfo.getFirstName() : null,
				userInfo != null ? userInfo.getLastName() : null,
				resolveEmail(userInfo),
				resolvePhoneCountryCode(userInfo),
				resolvePhoneNumber(userInfo),
				user.getStatus().name(),
				userRoleRepository.findPrimaryRoleCodeByUserId(user.getId()).orElse(null),
				userRoleRepository.findRoleCodesByUserId(user.getId()));
	}

	private String resolveGroup(Permission permission) {
		int separatorIndex = permission.getCode().indexOf('.');
		if (separatorIndex < 0) {
			return "general";
		}
		return permission.getCode().substring(0, separatorIndex);
	}

	private String buildFullName(UserInfo userInfo) {
		if (userInfo == null) {
			return null;
		}
		return (userInfo.getFirstName() + " " + userInfo.getLastName()).trim();
	}

	private String resolveEmail(UserInfo userInfo) {
		return userInfo != null && userInfo.getContact() != null ? userInfo.getContact().getEmail() : null;
	}

	private String resolvePhoneCountryCode(UserInfo userInfo) {
		return userInfo != null && userInfo.getContact() != null && userInfo.getContact().getPhone() != null
				? userInfo.getContact().getPhone().getCountryCode()
				: null;
	}

	private String resolvePhoneNumber(UserInfo userInfo) {
		return userInfo != null && userInfo.getContact() != null && userInfo.getContact().getPhone() != null
				? userInfo.getContact().getPhone().getNumber()
				: null;
	}
}
