package com.hospital.hospital.accesscontrol.dto;

import java.util.UUID;

public class RoleSummaryResponse {

	private UUID id;
	private String code;
	private String name;
	private String description;
	private boolean systemRole;
	private long permissionCount;

	public RoleSummaryResponse() {
	}

	public RoleSummaryResponse(UUID id, String code, String name, String description, boolean systemRole,
			long permissionCount) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.description = description;
		this.systemRole = systemRole;
		this.permissionCount = permissionCount;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isSystemRole() {
		return systemRole;
	}

	public void setSystemRole(boolean systemRole) {
		this.systemRole = systemRole;
	}

	public long getPermissionCount() {
		return permissionCount;
	}

	public void setPermissionCount(long permissionCount) {
		this.permissionCount = permissionCount;
	}
}
