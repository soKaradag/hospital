package com.hospital.hospital.doctorschedule.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctor_schedule_exceptions")
public class DoctorScheduleException extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doctor_schedule_id", nullable = false)
	private DoctorSchedule doctorSchedule;

	@Column(name = "exception_date", nullable = false)
	private LocalDate exceptionDate;

	@Column(name = "override_start_time")
	private LocalTime overrideStartTime;

	@Column(name = "override_end_time")
	private LocalTime overrideEndTime;

	@Column(name = "available", nullable = false)
	private boolean available = true;

	@Column(name = "note", length = 255)
	private String note;

	public DoctorSchedule getDoctorSchedule() {
		return doctorSchedule;
	}

	public void setDoctorSchedule(DoctorSchedule doctorSchedule) {
		this.doctorSchedule = doctorSchedule;
	}

	public LocalDate getExceptionDate() {
		return exceptionDate;
	}

	public void setExceptionDate(LocalDate exceptionDate) {
		this.exceptionDate = exceptionDate;
	}

	public LocalTime getOverrideStartTime() {
		return overrideStartTime;
	}

	public void setOverrideStartTime(LocalTime overrideStartTime) {
		this.overrideStartTime = overrideStartTime;
	}

	public LocalTime getOverrideEndTime() {
		return overrideEndTime;
	}

	public void setOverrideEndTime(LocalTime overrideEndTime) {
		this.overrideEndTime = overrideEndTime;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
