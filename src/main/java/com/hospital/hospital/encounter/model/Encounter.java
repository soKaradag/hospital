package com.hospital.hospital.encounter.model;

import java.time.Instant;

import com.hospital.hospital.appointment.model.Appointment;
import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.patient.model.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// Encounter, hastanın doktorla gerçekleşen klinik temasının çekirdek kaydıdır.
// İleriki fazlarda reçete, laboratuvar ve tanı detayları bu yapının etrafında genişler.
@Entity
@Table(name = "encounters")
public class Encounter extends BaseEntity {

	public Encounter() {
	}

	public Encounter(Appointment appointment, Patient patient, Doctor doctor, String complaint, String diagnosisNote,
			String treatmentNote, Instant encounterDateTime) {
		this.appointment = appointment;
		this.patient = patient;
		this.doctor = doctor;
		this.complaint = complaint;
		this.diagnosisNote = diagnosisNote;
		this.treatmentNote = treatmentNote;
		this.encounterDateTime = encounterDateTime;
	}

	// ManyToOne burada bir randevunun ileride farklı işlem kayıtlarıyla ilişkilendirilebilmesini esnek bırakır.
	@ManyToOne(fetch = FetchType.LAZY)
	// JoinColumn ise encounters tablosundaki appointment_id kolonunun appointments tablosuna bağlandığını belirtir.
	@JoinColumn(name = "appointment_id")
	private Appointment appointment;

	// ManyToOne burada bir hastanın zaman içinde birden fazla muayene kaydına sahip olabileceğini anlatır.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	// JoinColumn ise encounters tablosundaki patient_id kolonunun patients tablosuna bağlandığını belirtir.
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	// ManyToOne burada bir doktorun birden fazla muayene gerçekleştirebileceğini anlatır.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	// JoinColumn ise encounters tablosundaki doctor_id kolonunun doctors tablosuna bağlandığını belirtir.
	@JoinColumn(name = "doctor_id", nullable = false)
	private Doctor doctor;

	// Şikayet alanı hastanın başvuru nedenini çekirdek kayıt olarak taşır.
	@Column(name = "complaint", length = 500)
	private String complaint;

	// Tanı notu bu klinik temasın hekim değerlendirmesini ana seviyede saklar.
	@Column(name = "diagnosis_note", length = 1000)
	private String diagnosisNote;

	// Tedavi notu encounter sonucunda verilen temel klinik aksiyonu taşır.
	@Column(name = "treatment_note", length = 1000)
	private String treatmentNote;

	// Muayene zamanı gerçek işlem anını UTC olarak temsil eder.
	@Column(name = "encounter_date_time", nullable = false)
	private Instant encounterDateTime;

	public Appointment getAppointment() {
		return appointment;
	}

	public void setAppointment(Appointment appointment) {
		this.appointment = appointment;
	}

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

	public String getComplaint() {
		return complaint;
	}

	public void setComplaint(String complaint) {
		this.complaint = complaint;
	}

	public String getDiagnosisNote() {
		return diagnosisNote;
	}

	public void setDiagnosisNote(String diagnosisNote) {
		this.diagnosisNote = diagnosisNote;
	}

	public String getTreatmentNote() {
		return treatmentNote;
	}

	public void setTreatmentNote(String treatmentNote) {
		this.treatmentNote = treatmentNote;
	}

	public Instant getEncounterDateTime() {
		return encounterDateTime;
	}

	public void setEncounterDateTime(Instant encounterDateTime) {
		this.encounterDateTime = encounterDateTime;
	}
}
