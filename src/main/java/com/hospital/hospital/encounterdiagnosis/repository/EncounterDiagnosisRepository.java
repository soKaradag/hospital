package com.hospital.hospital.encounterdiagnosis.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.encounterdiagnosis.model.EncounterDiagnosis;

public interface EncounterDiagnosisRepository extends JpaRepository<EncounterDiagnosis, UUID> {

	Page<EncounterDiagnosis> findAllByEncounterId(UUID encounterId, Pageable pageable);

	Page<EncounterDiagnosis> findAllByDiseaseId(UUID diseaseId, Pageable pageable);
}
