package com.hospital.hospital.appointment.dto;

import java.time.Instant;
import java.util.UUID;

import com.hospital.hospital.appointment.model.AppointmentStatus;

public class AppointmentResponse {

	private UUID id;
	private UUID patientId;
	private String patientFullName;
	private UUID doctorId;
	private String doctorFullName;
	private Instant appointmentDateTime;
	private AppointmentStatus status;
	private String notes;
	private long statusHistoryCount;
	private long reminderCount;
	private Instant createdAt;
	private Instant updatedAt;

	public AppointmentResponse() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public Instant getAppointmentDateTime() {
		return appointmentDateTime;
	}

	public void setAppointmentDateTime(Instant appointmentDateTime) {
		this.appointmentDateTime = appointmentDateTime;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public long getStatusHistoryCount() {
		return statusHistoryCount;
	}

	public void setStatusHistoryCount(long statusHistoryCount) {
		this.statusHistoryCount = statusHistoryCount;
	}

	public long getReminderCount() {
		return reminderCount;
	}

	public void setReminderCount(long reminderCount) {
		this.reminderCount = reminderCount;
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
