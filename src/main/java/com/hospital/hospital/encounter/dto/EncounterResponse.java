package com.hospital.hospital.encounter.dto;

import java.time.Instant;
import java.util.UUID;

public class EncounterResponse {

	private UUID id;
	private UUID appointmentId;
	private UUID patientId;
	private String patientFullName;
	private UUID doctorId;
	private String doctorFullName;
	private String complaint;
	private String diagnosisNote;
	private String treatmentNote;
	private Instant encounterDateTime;
	private long vitalCount;
	private long procedureCount;
	private Instant createdAt;
	private Instant updatedAt;

	public EncounterResponse() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public String getPatientFullName() {
		return patientFullName;
	}

	public void setPatientFullName(String patientFullName) {
		this.patientFullName = patientFullName;
	}

	public UUID getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(UUID doctorId) {
		this.doctorId = doctorId;
	}

	public String getDoctorFullName() {
		return doctorFullName;
	}

	public void setDoctorFullName(String doctorFullName) {
		this.doctorFullName = doctorFullName;
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

	public long getVitalCount() {
		return vitalCount;
	}

	public void setVitalCount(long vitalCount) {
		this.vitalCount = vitalCount;
	}

	public long getProcedureCount() {
		return procedureCount;
	}

	public void setProcedureCount(long procedureCount) {
		this.procedureCount = procedureCount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
