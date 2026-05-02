package com.hospital.hospital.patient.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.patient.model.Patient;

// Patient tablosu için veri erişim işlemlerini yönetir.
public interface PatientRepository extends JpaRepository<Patient, UUID> {

	// Belirli bir ulusal kimlik numarasına sahip hastayı getirir.
	// Optional dönüş tipi, kayıt bulunmaması durumunu güvenli şekilde temsil eder.
	// findByNationalId, Spring Data JPA tarafından otomatik olarak oluşturulur.
	// Query: SELECT * FROM patients WHERE national_id = ?
	Optional<Patient> findByNationalId(String nationalId);

	// Belirli bir ulusal kimlik numarası ile hasta kaydı olup olmadığını kontrol eder.
	// Query: SELECT COUNT(*) > 0 FROM patients WHERE national_id = ?
	boolean existsByNationalId(String nationalId);

	// Hasta adı, soyadı veya ulusal kimlik bilgisine göre arama yapar.
	// Spring Data JPA, metot adındaki alanları OR koşulu ile birleştirir.
	// Query: SELECT * FROM patients
	//        WHERE LOWER(first_name) LIKE LOWER('%?%')
	//           OR LOWER(last_name) LIKE LOWER('%?%')
	//           OR national_id LIKE '%?%'
	Page<Patient> findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNationalIdContaining(
			String firstNameKeyword,
			String lastNameKeyword,
			String nationalIdKeyword,
			Pageable pageable);
}
