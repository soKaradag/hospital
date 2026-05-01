package com.hospital.hospital.patientdisease.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.patientdisease.model.PatientDiseaseStatusHistory;

public interface PatientDiseaseStatusHistoryRepository extends JpaRepository<PatientDiseaseStatusHistory, UUID> {

	long countByPatientDiseaseId(UUID patientDiseaseId);
}
