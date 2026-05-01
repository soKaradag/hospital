package com.hospital.hospital.prescription.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.prescription.model.Medication;

public interface MedicationRepository extends JpaRepository<Medication, UUID> {

	Optional<Medication> findByCode(String code);
}
