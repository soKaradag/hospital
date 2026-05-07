package com.hospital.hospital.accesscontrol.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateRoleRequest {

	@NotBlank
	@Size(max = 100)
	@Pattern(regexp = "^[A-Z][A-Z0-9_]*$")
	private String code;

	@NotBlank
	@Size(max = 100)
	private String name;

	@Size(max = 255)
	private String description;

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
}
