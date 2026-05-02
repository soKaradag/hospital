package com.hospital.hospital.surgery.model;

import java.time.LocalDate;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.encounter.model.Encounter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "surgery_requests")
public class SurgeryRequest extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "encounter_id", nullable = false)
	private Encounter encounter;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "requested_by_doctor_id", nullable = false)
	private Doctor requestedByDoctor;

	@Column(name = "procedure_code", nullable = false, length = 100)
	private String procedureCode;

	@Column(name = "procedure_name", nullable = false, length = 150)
	private String procedureName;

	@Column(name = "priority", nullable = false, length = 40)
	private String priority;

	@Column(name = "status", nullable = false, length = 40)
	private String status;

	@Column(name = "preferred_date")
	private LocalDate preferredDate;

	@Column(name = "note", length = 255)
	private String note;

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public Doctor getRequestedByDoctor() {
		return requestedByDoctor;
	}

	public void setRequestedByDoctor(Doctor requestedByDoctor) {
		this.requestedByDoctor = requestedByDoctor;
	}

	public String getProcedureCode() {
		return procedureCode;
	}

	public void setProcedureCode(String procedureCode) {
		this.procedureCode = procedureCode;
	}

	public String getProcedureName() {
		return procedureName;
	}

	public void setProcedureName(String procedureName) {
		this.procedureName = procedureName;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getPreferredDate() {
		return preferredDate;
	}

	public void setPreferredDate(LocalDate preferredDate) {
		this.preferredDate = preferredDate;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
