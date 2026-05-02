package com.hospital.hospital.patientdisease.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.patientdisease.model.PatientDiseaseFollowup;

public interface PatientDiseaseFollowupRepository extends JpaRepository<PatientDiseaseFollowup, UUID> {

	long countByPatientDiseaseId(UUID patientDiseaseId);
}
