package com.hospital.hospital.encounter.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.encounter.model.Encounter;

// Encounter tablosu için veri erişim işlemlerini yönetir.
public interface EncounterRepository extends JpaRepository<Encounter, UUID> {

	// Belirli bir hastaya ait tüm encounter kayıtlarını sayfalayarak getirir.
	// Pageable, sayfalama bilgilerini içerir.
	// findAllByPatientId, Spring Data JPA tarafından otomatik olarak oluşturulur.
	// Query: SELECT * FROM encounters WHERE patient_id = ?
	Page<Encounter> findAllByPatientId(UUID patientId, Pageable pageable);

	// Belirli bir doktora ait tüm encounter kayıtlarını sayfalayarak getirir.
	// Query: SELECT * FROM encounters WHERE doctor_id = ?
	Page<Encounter> findAllByDoctorId(UUID doctorId, Pageable pageable);

	// Belirli bir zaman aralığındaki encounter kayıtlarını sayfalayarak getirir.
	// Query: SELECT * FROM encounters WHERE encounter_date_time BETWEEN ? AND ?
	Page<Encounter> findAllByEncounterDateTimeBetween(Instant startInclusive, Instant endInclusive,
			Pageable pageable);

	// Şikayet, tanı notu veya tedavi notu içinde geçen metne göre arama yapar.
	// Query: SELECT * FROM encounters
	//        WHERE LOWER(complaint) LIKE LOWER('%?%')
	//           OR LOWER(diagnosis_note) LIKE LOWER('%?%')
	//           OR LOWER(treatment_note) LIKE LOWER('%?%')
	Page<Encounter> findAllByComplaintContainingIgnoreCaseOrDiagnosisNoteContainingIgnoreCaseOrTreatmentNoteContainingIgnoreCase(
			String complaintKeyword,
			String diagnosisKeyword,
			String treatmentKeyword,
			Pageable pageable);
}
