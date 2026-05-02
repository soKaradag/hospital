package com.hospital.hospital.encounter.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "encounter_procedures")
public class EncounterProcedure extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "encounter_id", nullable = false)
	private Encounter encounter;

	@Column(name = "procedure_code", nullable = false, length = 100)
	private String procedureCode;

	@Column(name = "procedure_name", nullable = false, length = 150)
	private String procedureName;

	@Column(name = "performed_at", nullable = false)
	private Instant performedAt;

	@Column(name = "note", length = 255)
	private String note;

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
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

	public Instant getPerformedAt() {
		return performedAt;
	}

	public void setPerformedAt(Instant performedAt) {
		this.performedAt = performedAt;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
