package com.hospital.hospital.appointment.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.appointment.model.Appointment;

// Appointment tablosu için veri erişim işlemlerini yönetir.
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

	// Belirli bir hastaya ait tüm randevuları sayfalayarak getirir.
	// Pageable, sayfalama bilgilerini içerir.
	// findAllByPatientId, Spring Data JPA tarafından otomatik olarak oluşturulur.
	// JPA, metot ismindeki patientId alanını patient tablosundaki id alanına bağlar.
	// Query: SELECT * FROM appointments WHERE patient_id = ?
	Page<Appointment> findAllByPatientId(UUID patientId, Pageable pageable);

	// Belirli bir doktora ait tüm randevuları sayfalayarak getirir.
	// JPA, metot ismindeki doctorId alanını doctor ilişkisinin id alanına bağlar.
	// Query: SELECT * FROM appointments WHERE doctor_id = ?
	Page<Appointment> findAllByDoctorId(UUID doctorId, Pageable pageable);

	// Belirli bir zaman aralığındaki tüm randevuları sayfalayarak getirir.
	// BETWEEN ifadesi başlangıç ve bitiş değerlerini kapsayacak şekilde çalışır.
	// Query: SELECT * FROM appointments WHERE appointment_date_time BETWEEN ? AND ?
	Page<Appointment> findAllByAppointmentDateTimeBetween(Instant startInclusive, Instant endInclusive,
			Pageable pageable);

	// Randevu notu içinde geçen metne göre arama yapar.
	// Query: SELECT * FROM appointments WHERE LOWER(notes) LIKE LOWER('%?%')
	Page<Appointment> findAllByNotesContainingIgnoreCase(String keyword, Pageable pageable);
}
