package com.hospital.hospital.encounter.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.encounter.model.EncounterVital;

public interface EncounterVitalRepository extends JpaRepository<EncounterVital, UUID> {

	long countByEncounterId(UUID encounterId);
}
