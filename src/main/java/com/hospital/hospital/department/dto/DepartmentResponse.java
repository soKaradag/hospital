package com.hospital.hospital.department.dto;

import java.time.Instant;
import java.util.UUID;

public class DepartmentResponse {

	private UUID id;
	private String name;
	private String description;
	private long roomCount;
	private long serviceCount;
	private Instant createdAt;
	private Instant updatedAt;

	public DepartmentResponse() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getRoomCount() {
		return roomCount;
	}

	public void setRoomCount(long roomCount) {
		this.roomCount = roomCount;
	}

	public long getServiceCount() {
		return serviceCount;
	}

	public void setServiceCount(long serviceCount) {
		this.serviceCount = serviceCount;
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
