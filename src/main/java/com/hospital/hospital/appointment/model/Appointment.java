package com.hospital.hospital.appointment.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.patient.model.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity {

	// Parametresiz kurucu metot jpa için gereklidir.
	public Appointment() {
	}

	// Parametreli kurucu metot.
	public Appointment(Patient patient, Doctor doctor, Instant appointmentDateTime, AppointmentStatus status,
			String notes) {
		this.patient = patient;
		this.doctor = doctor;
		this.appointmentDateTime = appointmentDateTime;
		this.status = status;
		this.notes = notes;
	}

	// ManyToOne burada bir hastanın birden fazla randevusu olabileceğini anlatır.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	// JoinColumn ise appointments tablosundaki patient_id kolonunun patients tablosuna bağlandığını belirtir.
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	// ManyToOne burada bir doktorun sistemde birden fazla randevuya sahip olabileceğini anlatır.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	// JoinColumn ise appointments tablosundaki doctor_id kolonunun doctors tablosuna bağlandığını belirtir.
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;

	// Randevu anı UTC olarak tutulur; backend ile veritabanı arasında saat kayması yaşanmaması hedeflenir.
	@Column(name = "appointment_date_time", nullable = false)
	private Instant appointmentDateTime;

	// Durum alanı randevu akışını yönetmek için enum olarak tutulur.
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 30)
	private AppointmentStatus status;

	// Operasyonel kısa notlar için serbest metin alanı bırakıldı.
	@Column(name = "notes", length = 500)
	private String notes;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}

	public Instant getAppointmentDateTime() {
		return appointmentDateTime;
	}

	public void setAppointmentDateTime(Instant appointmentDateTime) {
		this.appointmentDateTime = appointmentDateTime;
	}

	public AppointmentStatus getStatus() {
		return status;
	}

	public void setStatus(AppointmentStatus status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
