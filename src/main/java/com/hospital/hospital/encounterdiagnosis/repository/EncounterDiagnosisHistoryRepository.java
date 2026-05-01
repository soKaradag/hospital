package com.hospital.hospital.encounterdiagnosis.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.encounterdiagnosis.model.EncounterDiagnosisHistory;

public interface EncounterDiagnosisHistoryRepository extends JpaRepository<EncounterDiagnosisHistory, UUID> {

	long countByEncounterDiagnosisId(UUID encounterDiagnosisId);
}
