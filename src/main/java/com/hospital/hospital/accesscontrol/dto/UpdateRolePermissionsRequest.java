package com.hospital.hospital.accesscontrol.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public class UpdateRolePermissionsRequest {

	@NotNull
	private List<String> permissionCodes;

	public List<String> getPermissionCodes() {
		return permissionCodes;
	}

	public void setPermissionCodes(List<String> permissionCodes) {
		this.permissionCodes = permissionCodes;
	}
}
