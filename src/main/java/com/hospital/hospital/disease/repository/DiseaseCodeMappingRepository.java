package com.hospital.hospital.disease.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.disease.model.DiseaseCodeMapping;

public interface DiseaseCodeMappingRepository extends JpaRepository<DiseaseCodeMapping, UUID> {

	long countByDiseaseId(UUID diseaseId);

	Optional<DiseaseCodeMapping> findByDiseaseIdAndCodingSystem(UUID diseaseId, String codingSystem);
}
