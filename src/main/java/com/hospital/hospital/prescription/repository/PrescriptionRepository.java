package com.hospital.hospital.prescription.repository;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.prescription.model.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

	Page<Prescription> findAllByEncounterId(UUID encounterId, Pageable pageable);

	Page<Prescription> findAllByPatientId(UUID patientId, Pageable pageable);

	Page<Prescription> findAllByDoctorId(UUID doctorId, Pageable pageable);

	Page<Prescription> findAllByPrescriptionDateBetween(LocalDate startInclusive, LocalDate endInclusive, Pageable pageable);
}
