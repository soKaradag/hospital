package com.hospital.hospital.doctor.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.doctor.model.Doctor;

// Doctor tablosu için veri erişim işlemlerini yönetir.
public interface DoctorRepository extends JpaRepository<Doctor, UUID> {

	java.util.Optional<Doctor> findByIdAndActiveTrue(UUID id);

	Page<Doctor> findAllByActiveTrue(Pageable pageable);

	// Belirli bir bölüme ait tüm doktorları sayfalayarak getirir.
	// Pageable, sayfalama bilgilerini içerir.
	// findAllByDepartmentId, Spring Data JPA tarafından otomatik olarak oluşturulur.
	// JPA, metot ismindeki departmentId alanını department ilişkisinin id alanına bağlar.
	// Query: SELECT * FROM doctors WHERE department_id = ?
	Page<Doctor> findAllByDepartmentIdAndActiveTrue(UUID departmentId, Pageable pageable);

	// Doktor adı, soyadı veya uzmanlık bilgisi içinde geçen metne göre arama yapar.
	// Spring Data JPA, metot adındaki alanları OR koşulu ile birleştirir.
	// Query: SELECT * FROM doctors
	//        WHERE LOWER(first_name) LIKE LOWER('%?%')
	//           OR LOWER(last_name) LIKE LOWER('%?%')
	//           OR LOWER(specialization) LIKE LOWER('%?%')
	Page<Doctor> findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
			String firstNameKeyword,
			String lastNameKeyword,
			String specializationKeyword,
			Pageable pageable);

	Page<Doctor> findAllByActiveTrueAndFirstNameContainingIgnoreCaseOrActiveTrueAndLastNameContainingIgnoreCaseOrActiveTrueAndSpecializationContainingIgnoreCase(
			String firstNameKeyword,
			String lastNameKeyword,
			String specializationKeyword,
			Pageable pageable);
}
