package com.hospital.hospital.patientdisease.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.disease.model.Disease;
import com.hospital.hospital.patient.model.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
- Bu entity hastanın bilinen hastalık geçmişini tutar.
- Aynı hastalık birden fazla hastada bulunabildiği için ilişki tablosu ayrı tutulur.
*/
@Entity
@Table(name = "patient_diseases")
public class PatientDisease extends BaseEntity {

	public PatientDisease() {
	}

	public PatientDisease(Patient patient, Disease disease, Instant diagnosedAt, String notes) {
		this.patient = patient;
		this.disease = disease;
		this.diagnosedAt = diagnosedAt;
		this.notes = notes;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "disease_id", nullable = false)
	private Disease disease;

	@Column(name = "diagnosed_at")
	private Instant diagnosedAt;

	@Column(name = "notes", length = 1000)
	private String notes;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
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
