package com.hospital.hospital.patient.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.patient.model.PatientInsurance;

public interface PatientInsuranceRepository extends JpaRepository<PatientInsurance, UUID> {

	long countByPatientId(UUID patientId);
}
