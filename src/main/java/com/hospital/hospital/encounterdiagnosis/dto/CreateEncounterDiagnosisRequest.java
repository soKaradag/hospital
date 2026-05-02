package com.hospital.hospital.encounterdiagnosis.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateEncounterDiagnosisRequest {

	@NotNull(message = "encounterId must not be null")
	private UUID encounterId;

	@NotNull(message = "diseaseId must not be null")
	private UUID diseaseId;

	@Size(max = 1000, message = "notes must be at most 1000 characters")
	private String notes;

	public UUID getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(UUID encounterId) {
		this.encounterId = encounterId;
	}

	public UUID getDiseaseId() {
		return diseaseId;
	}

	public void setDiseaseId(UUID diseaseId) {
		this.diseaseId = diseaseId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
