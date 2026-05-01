package com.hospital.hospital.patient.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.patient.model.PatientEmergencyContact;

public interface PatientEmergencyContactRepository extends JpaRepository<PatientEmergencyContact, UUID> {

	long countByPatientId(UUID patientId);
}
