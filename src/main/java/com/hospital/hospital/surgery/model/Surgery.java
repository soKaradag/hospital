package com.hospital.hospital.surgery.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.patient.model.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "surgeries")
public class Surgery extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "surgery_request_id", nullable = false)
	private SurgeryRequest surgeryRequest;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "primary_doctor_id", nullable = false)
	private Doctor primaryDoctor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "operating_room_id", nullable = false)
	private OperatingRoom operatingRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "supply_template_id")
	private SurgerySupplyTemplate supplyTemplate;

	@Column(name = "scheduled_at", nullable = false)
	private Instant scheduledAt;

	@Column(name = "status", nullable = false, length = 40)
	private String status;

	@Column(name = "inventory_status", nullable = false, length = 40)
	private String inventoryStatus;

	@Column(name = "note", length = 255)
	private String note;

	public SurgeryRequest getSurgeryRequest() {
		return surgeryRequest;
	}

	public void setSurgeryRequest(SurgeryRequest surgeryRequest) {
		this.surgeryRequest = surgeryRequest;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Doctor getPrimaryDoctor() {
		return primaryDoctor;
	}

	public void setPrimaryDoctor(Doctor primaryDoctor) {
		this.primaryDoctor = primaryDoctor;
	}

	public OperatingRoom getOperatingRoom() {
		return operatingRoom;
	}

	public void setOperatingRoom(OperatingRoom operatingRoom) {
		this.operatingRoom = operatingRoom;
	}

	public SurgerySupplyTemplate getSupplyTemplate() {
		return supplyTemplate;
	}

	public void setSupplyTemplate(SurgerySupplyTemplate supplyTemplate) {
		this.supplyTemplate = supplyTemplate;
	}

	public Instant getScheduledAt() {
		return scheduledAt;
	}

	public void setScheduledAt(Instant scheduledAt) {
		this.scheduledAt = scheduledAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInventoryStatus() {
		return inventoryStatus;
	}

	public void setInventoryStatus(String inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
