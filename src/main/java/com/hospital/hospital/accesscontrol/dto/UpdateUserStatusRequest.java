package com.hospital.hospital.accesscontrol.dto;

import com.hospital.hospital.auth.model.UserStatus;

import jakarta.validation.constraints.NotNull;

public class UpdateUserStatusRequest {

	@NotNull
	private UserStatus status;

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}
}
