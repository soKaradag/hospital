package com.hospital.hospital.patientdisease.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "patient_disease_status_history")
public class PatientDiseaseStatusHistory extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "patient_disease_id", nullable = false)
	private PatientDisease patientDisease;

	@Column(name = "status", nullable = false, length = 50)
	private String status;

	@Column(name = "noted_at", nullable = false)
	private Instant notedAt;

	@Column(name = "note", length = 500)
	private String note;

	public PatientDisease getPatientDisease() {
		return patientDisease;
	}

	public void setPatientDisease(PatientDisease patientDisease) {
		this.patientDisease = patientDisease;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getNotedAt() {
		return notedAt;
	}

	public void setNotedAt(Instant notedAt) {
		this.notedAt = notedAt;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
