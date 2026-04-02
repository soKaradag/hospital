package com.hospital.hospital.reporting.dto;

import java.util.UUID;

// View üzerinden dönen doktor iş yükü özetini API response seviyesinde taşır.
public class DoctorWorkloadReportResponse {

	private UUID doctorId;
	private String doctorFullName;
	private String departmentName;
	private long appointmentCount;
	private long encounterCount;
	private long prescriptionCount;

	public DoctorWorkloadReportResponse() {
	}

	public DoctorWorkloadReportResponse(UUID doctorId, String doctorFullName, String departmentName,
			long appointmentCount, long encounterCount, long prescriptionCount) {
		this.doctorId = doctorId;
		this.doctorFullName = doctorFullName;
		this.departmentName = departmentName;
		this.appointmentCount = appointmentCount;
		this.encounterCount = encounterCount;
		this.prescriptionCount = prescriptionCount;
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

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public long getAppointmentCount() {
		return appointmentCount;
	}

	public void setAppointmentCount(long appointmentCount) {
		this.appointmentCount = appointmentCount;
	}

	public long getEncounterCount() {
		return encounterCount;
	}

	public void setEncounterCount(long encounterCount) {
		this.encounterCount = encounterCount;
	}

	public long getPrescriptionCount() {
		return prescriptionCount;
	}

	public void setPrescriptionCount(long prescriptionCount) {
		this.prescriptionCount = prescriptionCount;
	}
}
