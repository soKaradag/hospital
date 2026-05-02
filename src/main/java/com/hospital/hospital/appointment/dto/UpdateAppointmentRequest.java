package com.hospital.hospital.appointment.dto;

import java.time.Instant;
import java.util.UUID;

import com.hospital.hospital.appointment.model.AppointmentStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateAppointmentRequest {

	@NotNull
	private UUID patientId;

	@NotNull
	private UUID doctorId;

	@NotNull
	private Instant appointmentDateTime;

	@NotNull
	private AppointmentStatus status;

	@Size(max = 500)
	private String notes;

	public UpdateAppointmentRequest() {
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
}
