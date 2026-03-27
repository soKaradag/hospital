package com.hospital.hospital.encounterdiagnosis.model;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.disease.model.Disease;
import com.hospital.hospital.encounter.model.Encounter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
- Bu entity belirli bir encounter sırasında konulan teşhisi temsil eder.
- Disease kataloğu ile ilişki kurarak serbest metin yerine standartlaştırılmış teşhis yapısına alan açar.
*/
@Entity
@Table(name = "encounter_diagnoses")
public class EncounterDiagnosis extends BaseEntity {

	public EncounterDiagnosis() {
	}

	public EncounterDiagnosis(Encounter encounter, Disease disease, String notes) {
		this.encounter = encounter;
		this.disease = disease;
		this.notes = notes;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "encounter_id", nullable = false)
	private Encounter encounter;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "disease_id", nullable = false)
	private Disease disease;

	@Column(name = "notes", length = 1000)
	private String notes;

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
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
}
