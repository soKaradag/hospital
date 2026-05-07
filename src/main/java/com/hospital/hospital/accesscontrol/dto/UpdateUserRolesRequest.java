package com.hospital.hospital.accesscontrol.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class UpdateUserRolesRequest {

	@NotEmpty
	private List<UUID> roleIds;

	@NotNull
	private UUID primaryRoleId;

	public List<UUID> getRoleIds() {
		return roleIds;
	}

	public void setRoleIds(List<UUID> roleIds) {
		this.roleIds = roleIds;
	}

	public UUID getPrimaryRoleId() {
		return primaryRoleId;
	}

	public void setPrimaryRoleId(UUID primaryRoleId) {
		this.primaryRoleId = primaryRoleId;
	}
}
