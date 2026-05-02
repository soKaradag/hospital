package com.hospital.hospital.surgery.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CreateSurgerySupplyTemplateRequest {

	@NotBlank(message = "code must not be blank")
	@Size(max = 100, message = "code must be at most 100 characters")
	private String code;

	@NotBlank(message = "name must not be blank")
	@Size(max = 150, message = "name must be at most 150 characters")
	private String name;

	@NotBlank(message = "procedureCode must not be blank")
	@Size(max = 100, message = "procedureCode must be at most 100 characters")
	private String procedureCode;

	private boolean active = true;

	@NotEmpty(message = "items must not be empty")
	@Valid
	private List<SurgerySupplyTemplateItemRequest> items;

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

	public String getProcedureCode() {
		return procedureCode;
	}

	public void setProcedureCode(String procedureCode) {
		this.procedureCode = procedureCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<SurgerySupplyTemplateItemRequest> getItems() {
		return items;
	}

	public void setItems(List<SurgerySupplyTemplateItemRequest> items) {
		this.items = items;
	}
}
