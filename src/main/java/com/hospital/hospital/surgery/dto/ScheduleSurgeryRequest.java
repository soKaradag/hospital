package com.hospital.hospital.surgery.dto;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ScheduleSurgeryRequest {

	@NotNull(message = "surgeryRequestId must not be null")
	private UUID surgeryRequestId;

	@NotNull(message = "primaryDoctorId must not be null")
	private UUID primaryDoctorId;

	@NotNull(message = "operatingRoomId must not be null")
	private UUID operatingRoomId;

	@NotNull(message = "scheduledAt must not be null")
	private Instant scheduledAt;

	@Size(max = 255, message = "note must be at most 255 characters")
	private String note;

	public UUID getSurgeryRequestId() {
		return surgeryRequestId;
	}

	public void setSurgeryRequestId(UUID surgeryRequestId) {
		this.surgeryRequestId = surgeryRequestId;
	}

	public UUID getPrimaryDoctorId() {
		return primaryDoctorId;
	}

	public void setPrimaryDoctorId(UUID primaryDoctorId) {
		this.primaryDoctorId = primaryDoctorId;
	}

	public UUID getOperatingRoomId() {
		return operatingRoomId;
	}

	public void setOperatingRoomId(UUID operatingRoomId) {
		this.operatingRoomId = operatingRoomId;
	}

	public Instant getScheduledAt() {
		return scheduledAt;
	}

	public void setScheduledAt(Instant scheduledAt) {
		this.scheduledAt = scheduledAt;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
