package com.hospital.hospital.accesscontrol.dto;

import java.util.List;
import java.util.UUID;

public class UserSummaryResponse {

	private UUID id;
	private String username;
	private String fullName;
	private String email;
	private String status;
	private String primaryRole;
	private long roleCount;
	private List<String> roles;

	public UserSummaryResponse() {
	}

	public UserSummaryResponse(UUID id, String username, String fullName, String email, String status,
			String primaryRole, long roleCount, List<String> roles) {
		this.id = id;
		this.username = username;
		this.fullName = fullName;
		this.email = email;
		this.status = status;
		this.primaryRole = primaryRole;
		this.roleCount = roleCount;
		this.roles = roles;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPrimaryRole() {
		return primaryRole;
	}

	public void setPrimaryRole(String primaryRole) {
		this.primaryRole = primaryRole;
	}

	public long getRoleCount() {
		return roleCount;
	}

	public void setRoleCount(long roleCount) {
		this.roleCount = roleCount;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
