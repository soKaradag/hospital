package com.hospital.hospital.encounterdiagnosis.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.disease.model.Disease;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "encounter_diagnosis_history")
public class EncounterDiagnosisHistory extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "encounter_diagnosis_id", nullable = false)
	private EncounterDiagnosis encounterDiagnosis;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "disease_id", nullable = false)
	private Disease disease;

	@Column(name = "notes", length = 1000)
	private String notes;

	@Column(name = "revised_at", nullable = false)
	private Instant revisedAt;

	public EncounterDiagnosis getEncounterDiagnosis() {
		return encounterDiagnosis;
	}

	public void setEncounterDiagnosis(EncounterDiagnosis encounterDiagnosis) {
		this.encounterDiagnosis = encounterDiagnosis;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Instant getRevisedAt() {
		return revisedAt;
	}

	public void setRevisedAt(Instant revisedAt) {
		this.revisedAt = revisedAt;
	}
}
