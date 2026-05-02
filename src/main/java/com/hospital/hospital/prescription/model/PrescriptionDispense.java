package com.hospital.hospital.prescription.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prescription_dispenses")
public class PrescriptionDispense extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "prescription_item_id", nullable = false)
	private PrescriptionItem prescriptionItem;

	@Column(name = "dispensed_at", nullable = false)
	private Instant dispensedAt;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "status", nullable = false, length = 50)
	private String status;

	@Column(name = "note", length = 255)
	private String note;

	public PrescriptionItem getPrescriptionItem() {
		return prescriptionItem;
	}

	public void setPrescriptionItem(PrescriptionItem prescriptionItem) {
		this.prescriptionItem = prescriptionItem;
	}

	public Instant getDispensedAt() {
		return dispensedAt;
	}

	public void setDispensedAt(Instant dispensedAt) {
		this.dispensedAt = dispensedAt;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
