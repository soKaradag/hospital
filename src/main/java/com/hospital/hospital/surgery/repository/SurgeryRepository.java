package com.hospital.hospital.surgery.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.surgery.model.Surgery;

public interface SurgeryRepository extends JpaRepository<Surgery, UUID> {

	boolean existsBySurgeryRequestId(UUID surgeryRequestId);

	@Override
	@EntityGraph(attributePaths = { "surgeryRequest", "patient", "primaryDoctor", "operatingRoom", "supplyTemplate", "supplyTemplate.items" })
	Optional<Surgery> findById(UUID id);
}
