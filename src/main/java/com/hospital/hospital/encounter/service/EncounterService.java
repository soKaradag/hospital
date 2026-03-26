package com.hospital.hospital.encounter.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.encounter.dto.CreateEncounterRequest;
import com.hospital.hospital.encounter.dto.EncounterResponse;
import com.hospital.hospital.encounter.dto.UpdateEncounterRequest;

public interface EncounterService {

	EncounterResponse create(CreateEncounterRequest request);

	EncounterResponse update(UUID id, UpdateEncounterRequest request);

	EncounterResponse getById(UUID id);

	Page<EncounterResponse> getAll(Pageable pageable);

	Page<EncounterResponse> getAllByPatient(UUID patientId, Pageable pageable);

	Page<EncounterResponse> getAllByDoctor(UUID doctorId, Pageable pageable);

	Page<EncounterResponse> getAllByDateRange(Instant startInclusive, Instant endInclusive, Pageable pageable);

	Page<EncounterResponse> search(String keyword, Pageable pageable);
}
