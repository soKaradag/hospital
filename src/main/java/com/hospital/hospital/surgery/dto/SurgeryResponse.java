package com.hospital.hospital.surgery.dto;

import java.time.Instant;
import java.util.UUID;

public class SurgeryResponse {

	private UUID id;
	private UUID surgeryRequestId;
	private UUID patientId;
	private UUID primaryDoctorId;
	private UUID operatingRoomId;
	private Instant scheduledAt;
	private String status;
	private String inventoryStatus;
	private String note;
	private long teamCount;
	private long historyCount;
	private Instant createdAt;
	private Instant updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getSurgeryRequestId() {
		return surgeryRequestId;
	}

	public void setSurgeryRequestId(UUID surgeryRequestId) {
		this.surgeryRequestId = surgeryRequestId;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInventoryStatus() {
		return inventoryStatus;
	}

	public void setInventoryStatus(String inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public long getTeamCount() {
		return teamCount;
	}

	public void setTeamCount(long teamCount) {
		this.teamCount = teamCount;
	}

	public long getHistoryCount() {
		return historyCount;
	}

	public void setHistoryCount(long historyCount) {
		this.historyCount = historyCount;
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
