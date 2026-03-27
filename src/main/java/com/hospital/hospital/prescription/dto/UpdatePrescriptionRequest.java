package com.hospital.hospital.prescription.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdatePrescriptionRequest {

	@NotNull(message = "encounterId must not be null")
	private UUID encounterId;

	@NotNull(message = "patientId must not be null")
	private UUID patientId;

	@NotNull(message = "doctorId must not be null")
	private UUID doctorId;

	@NotNull(message = "prescriptionDate must not be null")
	private LocalDate prescriptionDate;

	@Size(max = 1000, message = "notes must be at most 1000 characters")
	private String notes;

	public UUID getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(UUID encounterId) {
		this.encounterId = encounterId;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public UUID getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(UUID doctorId) {
		this.doctorId = doctorId;
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
