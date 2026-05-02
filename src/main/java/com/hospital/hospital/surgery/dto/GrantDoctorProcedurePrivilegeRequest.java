package com.hospital.hospital.surgery.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GrantDoctorProcedurePrivilegeRequest {

	@NotNull(message = "doctorId must not be null")
	private UUID doctorId;

	@NotBlank(message = "procedureCode must not be blank")
	@Size(max = 100, message = "procedureCode must be at most 100 characters")
	private String procedureCode;

	@NotBlank(message = "procedureName must not be blank")
	@Size(max = 150, message = "procedureName must be at most 150 characters")
	private String procedureName;

	private boolean active = true;

	public UUID getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(UUID doctorId) {
		this.doctorId = doctorId;
	}

	public String getProcedureCode() {
		return procedureCode;
	}

	public void setProcedureCode(String procedureCode) {
		this.procedureCode = procedureCode;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
