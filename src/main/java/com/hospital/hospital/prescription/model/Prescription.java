package com.hospital.hospital.prescription.model;

import java.time.LocalDate;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.patient.model.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/*
- Prescription entity encounter sonrası oluşan reçete üst kaydını temsil eder.
- Faz 2'de reçete satır detayına inmeden üst seviye klinik kayıt oluşturmak hedeflenir.
*/
@Entity
@Table(name = "prescriptions")
public class Prescription extends BaseEntity {

	public Prescription() {
	}

	public Prescription(Encounter encounter, Patient patient, Doctor doctor, LocalDate prescriptionDate, String notes) {
		this.encounter = encounter;
		this.patient = patient;
		this.doctor = doctor;
		this.prescriptionDate = prescriptionDate;
		this.notes = notes;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "encounter_id", nullable = false)
	private Encounter encounter;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;

	@Column(name = "prescription_date", nullable = false)
	private LocalDate prescriptionDate;

	@Column(name = "notes", length = 1000)
	private String notes;

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public LocalDate getPrescriptionDate() {
		return prescriptionDate;
	}

	public void setPrescriptionDate(LocalDate prescriptionDate) {
		this.prescriptionDate = prescriptionDate;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
