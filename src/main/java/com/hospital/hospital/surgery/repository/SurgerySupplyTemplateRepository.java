package com.hospital.hospital.surgery.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.surgery.model.SurgerySupplyTemplate;

public interface SurgerySupplyTemplateRepository extends JpaRepository<SurgerySupplyTemplate, UUID> {

	boolean existsByCodeIgnoreCase(String code);

	@Override
	@EntityGraph(attributePaths = { "items" })
	Optional<SurgerySupplyTemplate> findById(UUID id);
}
