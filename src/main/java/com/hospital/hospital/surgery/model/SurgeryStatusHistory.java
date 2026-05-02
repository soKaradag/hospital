package com.hospital.hospital.surgery.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "surgery_status_history")
public class SurgeryStatusHistory extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "surgery_id", nullable = false)
	private Surgery surgery;

	@Column(name = "status", nullable = false, length = 40)
	private String status;

	@Column(name = "changed_at", nullable = false)
	private Instant changedAt;

	@Column(name = "note", length = 255)
	private String note;

	public Surgery getSurgery() {
		return surgery;
	}

	public void setSurgery(Surgery surgery) {
		this.surgery = surgery;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getChangedAt() {
		return changedAt;
	}

	public void setChangedAt(Instant changedAt) {
		this.changedAt = changedAt;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
