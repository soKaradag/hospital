package com.hospital.hospital.prescription.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.prescription.model.PrescriptionDispense;

public interface PrescriptionDispenseRepository extends JpaRepository<PrescriptionDispense, UUID> {

	long countByPrescriptionItemPrescriptionId(UUID prescriptionId);

	Optional<PrescriptionDispense> findFirstByPrescriptionItemId(UUID prescriptionItemId);
}
