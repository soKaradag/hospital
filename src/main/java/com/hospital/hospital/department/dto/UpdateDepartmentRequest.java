package com.hospital.hospital.department.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateDepartmentRequest {

	@NotBlank
	@Size(max = 100)
	private String name;

	@Size(max = 255)
	private String description;

	public UpdateDepartmentRequest() {
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
}
