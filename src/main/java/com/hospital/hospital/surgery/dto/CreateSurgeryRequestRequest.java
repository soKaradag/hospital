package com.hospital.hospital.surgery.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateSurgeryRequestRequest {

	@NotNull(message = "encounterId must not be null")
	private UUID encounterId;

	@NotNull(message = "requestedByDoctorId must not be null")
	private UUID requestedByDoctorId;

	@NotBlank(message = "procedureCode must not be blank")
	@Size(max = 100, message = "procedureCode must be at most 100 characters")
	private String procedureCode;

	@NotBlank(message = "procedureName must not be blank")
	@Size(max = 150, message = "procedureName must be at most 150 characters")
	private String procedureName;

	@NotBlank(message = "priority must not be blank")
	@Size(max = 40, message = "priority must be at most 40 characters")
	private String priority;

	private LocalDate preferredDate;

	@Size(max = 255, message = "note must be at most 255 characters")
	private String note;

	public UUID getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(UUID encounterId) {
		this.encounterId = encounterId;
	}

	public UUID getRequestedByDoctorId() {
		return requestedByDoctorId;
	}

	public void setRequestedByDoctorId(UUID requestedByDoctorId) {
		this.requestedByDoctorId = requestedByDoctorId;
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

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public LocalDate getPreferredDate() {
		return preferredDate;
	}

	public void setPreferredDate(LocalDate preferredDate) {
		this.preferredDate = preferredDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
