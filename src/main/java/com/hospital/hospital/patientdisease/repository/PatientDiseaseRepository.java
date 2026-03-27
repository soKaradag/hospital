package com.hospital.hospital.patientdisease.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.patientdisease.model.PatientDisease;

public interface PatientDiseaseRepository extends JpaRepository<PatientDisease, UUID> {

	Page<PatientDisease> findAllByPatientId(UUID patientId, Pageable pageable);

	Page<PatientDisease> findAllByDiseaseId(UUID diseaseId, Pageable pageable);

	boolean existsByPatientIdAndDiseaseId(UUID patientId, UUID diseaseId);
}
