package com.hospital.hospital.surgery.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.doctor.model.Doctor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "doctor_procedure_privileges")
public class DoctorProcedurePrivilege extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;

	@Column(name = "procedure_code", nullable = false, length = 100)
	private String procedureCode;

	@Column(name = "procedure_name", nullable = false, length = 150)
	private String procedureName;

	@Column(name = "active", nullable = false)
	private boolean active;

	@Column(name = "granted_at", nullable = false)
	private Instant grantedAt;

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Instant getGrantedAt() {
		return grantedAt;
	}

	public void setGrantedAt(Instant grantedAt) {
		this.grantedAt = grantedAt;
	}
}
