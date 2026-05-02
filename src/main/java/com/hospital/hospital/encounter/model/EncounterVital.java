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
@Table(name = "encounter_vitals")
public class EncounterVital extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "encounter_id", nullable = false)
	private Encounter encounter;

	@Column(name = "vital_type", nullable = false, length = 50)
	private String vitalType;

	@Column(name = "vital_value", nullable = false, length = 100)
	private String vitalValue;

	@Column(name = "measured_at", nullable = false)
	private Instant measuredAt;

	@Column(name = "note", length = 255)
	private String note;

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}

	public String getVitalType() {
		return vitalType;
	}

	public void setVitalType(String vitalType) {
		this.vitalType = vitalType;
	}

	public String getVitalValue() {
		return vitalValue;
	}

	public void setVitalValue(String vitalValue) {
		this.vitalValue = vitalValue;
	}

	public Instant getMeasuredAt() {
		return measuredAt;
	}

	public void setMeasuredAt(Instant measuredAt) {
		this.measuredAt = measuredAt;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
