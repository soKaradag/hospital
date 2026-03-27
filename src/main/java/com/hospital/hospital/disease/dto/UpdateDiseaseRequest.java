package com.hospital.hospital.disease.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateDiseaseRequest {

	@NotBlank(message = "code must not be blank")
	@Size(max = 50, message = "code must be at most 50 characters")
	private String code;

	@NotBlank(message = "name must not be blank")
	@Size(max = 150, message = "name must be at most 150 characters")
	private String name;

	@Size(max = 500, message = "description must be at most 500 characters")
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
