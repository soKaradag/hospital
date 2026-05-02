package com.hospital.hospital.surgery.dto;

import java.time.Instant;
import java.util.UUID;

public class DoctorProcedurePrivilegeResponse {

	private UUID id;
	private UUID doctorId;
	private String procedureCode;
	private String procedureName;
	private boolean active;
	private Instant grantedAt;
	private Instant createdAt;
	private Instant updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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

	public Instant getGrantedAt() {
		return grantedAt;
	}

	public void setGrantedAt(Instant grantedAt) {
		this.grantedAt = grantedAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
