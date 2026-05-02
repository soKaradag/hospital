package com.hospital.hospital.prescription.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.prescription.model.PrescriptionItem;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, UUID> {

	long countByPrescriptionId(UUID prescriptionId);

	Optional<PrescriptionItem> findFirstByPrescriptionId(UUID prescriptionId);
}
