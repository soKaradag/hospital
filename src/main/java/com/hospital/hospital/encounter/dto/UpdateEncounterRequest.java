package com.hospital.hospital.encounter.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateEncounterRequest {

	private UUID appointmentId;

	@NotNull
	private UUID patientId;

	@NotNull
	private UUID doctorId;

	@Size(max = 500)
	private String complaint;

	@Size(max = 1000)
	private String diagnosisNote;

	@Size(max = 1000)
	private String treatmentNote;

	@NotNull
	private Instant encounterDateTime;

	public UpdateEncounterRequest() {
	}

	public UUID getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(UUID appointmentId) {
		this.appointmentId = appointmentId;
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

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getDiagnosisNote() {
		return diagnosisNote;
	}

	public void setDiagnosisNote(String diagnosisNote) {
		this.diagnosisNote = diagnosisNote;
	}

	public String getTreatmentNote() {
		return treatmentNote;
	}

	public void setTreatmentNote(String treatmentNote) {
		this.treatmentNote = treatmentNote;
	}

	public Instant getEncounterDateTime() {
		return encounterDateTime;
	}

	public void setEncounterDateTime(Instant encounterDateTime) {
		this.encounterDateTime = encounterDateTime;
	}
}
