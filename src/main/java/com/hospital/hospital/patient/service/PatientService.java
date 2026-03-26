package com.hospital.hospital.patient.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.patient.dto.CreatePatientRequest;
import com.hospital.hospital.patient.dto.PatientResponse;
import com.hospital.hospital.patient.dto.UpdatePatientRequest;

public interface PatientService {

	PatientResponse create(CreatePatientRequest request);

	PatientResponse update(UUID id, UpdatePatientRequest request);

	PatientResponse getById(UUID id);

	Page<PatientResponse> getAll(Pageable pageable);

	Page<PatientResponse> search(String keyword, Pageable pageable);
}
