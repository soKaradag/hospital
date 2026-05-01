package com.hospital.hospital.encounter.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.encounter.model.EncounterProcedure;

public interface EncounterProcedureRepository extends JpaRepository<EncounterProcedure, UUID> {

	long countByEncounterId(UUID encounterId);

	Optional<EncounterProcedure> findByEncounterIdAndProcedureCode(UUID encounterId, String procedureCode);
}
