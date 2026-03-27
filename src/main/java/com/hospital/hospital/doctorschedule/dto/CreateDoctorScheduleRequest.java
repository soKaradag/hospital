package com.hospital.hospital.doctorschedule.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public class CreateDoctorScheduleRequest {

	@NotNull(message = "doctorId must not be null")
	private UUID doctorId;

	@NotNull(message = "dayOfWeek must not be null")
	private DayOfWeek dayOfWeek;

	@NotNull(message = "startTime must not be null")
	private LocalTime startTime;

	@NotNull(message = "endTime must not be null")
	private LocalTime endTime;

	private boolean active = true;

	public UUID getDoctorId() {
		return doctorId;
	}

	public void setDoctorId(UUID doctorId) {
		this.doctorId = doctorId;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public LocalTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalTime startTime) {
		this.startTime = startTime;
	}

	public LocalTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalTime endTime) {
		this.endTime = endTime;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
