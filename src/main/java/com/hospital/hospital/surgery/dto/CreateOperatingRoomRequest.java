package com.hospital.hospital.surgery.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateOperatingRoomRequest {

	@NotNull(message = "departmentId must not be null")
	private UUID departmentId;

	@NotBlank(message = "code must not be blank")
	@Size(max = 100, message = "code must be at most 100 characters")
	private String code;

	@NotBlank(message = "name must not be blank")
	@Size(max = 150, message = "name must be at most 150 characters")
	private String name;

	private boolean active = true;

	public UUID getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(UUID departmentId) {
		this.departmentId = departmentId;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
