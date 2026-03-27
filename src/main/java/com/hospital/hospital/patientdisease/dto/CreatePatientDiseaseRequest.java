package com.hospital.hospital.patientdisease.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePatientDiseaseRequest {

	@NotNull(message = "patientId must not be null")
	private UUID patientId;

	@NotNull(message = "diseaseId must not be null")
	private UUID diseaseId;

	private Instant diagnosedAt;

	@Size(max = 1000, message = "notes must be at most 1000 characters")
	private String notes;

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public UUID getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(UUID diseaseId) {
		this.diseaseId = diseaseId;
	}

	public Instant getDiagnosedAt() {
		return diagnosedAt;
	}

	public void setDiagnosedAt(Instant diagnosedAt) {
		this.diagnosedAt = diagnosedAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
