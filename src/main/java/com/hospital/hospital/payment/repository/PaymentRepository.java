package com.hospital.hospital.payment.repository;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.payment.model.Payment;

// Payment tablosu için veri erişim işlemlerini yönetir.
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	// Belirli bir hastaya ait tüm ödeme kayıtlarını sayfalayarak getirir.
	// Pageable, sayfalama bilgilerini içerir.
	// findAllByPatientId, Spring Data JPA tarafından otomatik olarak oluşturulur.
	// Query: SELECT * FROM payments WHERE patient_id = ?
	Page<Payment> findAllByPatientId(UUID patientId, Pageable pageable);

	// Belirli bir muayene kaydına bağlı tüm ödemeleri sayfalayarak getirir.
	// Query: SELECT * FROM payments WHERE encounter_id = ?
	Page<Payment> findAllByEncounterId(UUID encounterId, Pageable pageable);

	// Belirli bir ödeme zaman aralığındaki kayıtları sayfalayarak getirir.
	// Query: SELECT * FROM payments WHERE paid_at BETWEEN ? AND ?
	Page<Payment> findAllByPaidAtBetween(Instant startInclusive, Instant endInclusive, Pageable pageable);
}
