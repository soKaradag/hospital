package com.hospital.hospital.patient.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.patient.dto.CreatePatientRequest;
import com.hospital.hospital.patient.dto.PatientResponse;
import com.hospital.hospital.patient.dto.UpdatePatientRequest;
import com.hospital.hospital.patient.mapper.PatientMapper;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;

@Service
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;
	private final PatientMapper patientMapper;

	public PatientServiceImpl(PatientRepository patientRepository, PatientMapper patientMapper) {
		this.patientRepository = patientRepository;
		this.patientMapper = patientMapper;
	}

	@Override
	@Transactional
	public PatientResponse create(CreatePatientRequest request) {
		validateNationalIdForCreate(request.getNationalId());
		Patient patient = patientMapper.toEntity(request);
		return patientMapper.toResponse(patientRepository.save(patient));
	}

	@Override
	@Transactional
	public PatientResponse update(UUID id, UpdatePatientRequest request) {
		Patient patient = getPatient(id);
		validateNationalIdForUpdate(id, request.getNationalId());
		patientMapper.updateEntity(request, patient);
		return patientMapper.toResponse(patientRepository.save(patient));
	}

	@Override
	@Transactional(readOnly = true)
	public PatientResponse getById(UUID id) {
		return patientMapper.toResponse(getPatient(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PatientResponse> getAll(Pageable pageable) {
		return patientRepository.findAll(pageable).map(patientMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PatientResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String value = keyword.trim();
		return patientRepository
				.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrNationalIdContaining(
						value, value, value, pageable)
				.map(patientMapper::toResponse);
	}

	private Patient getPatient(UUID id) {
		return patientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
	}

	private void validateNationalIdForCreate(String nationalId) {
		if (nationalId != null && !nationalId.isBlank() && patientRepository.existsByNationalId(nationalId)) {
			throw new DuplicateResourceException("Patient nationalId already exists: " + nationalId);
		}
	}

	private void validateNationalIdForUpdate(UUID id, String nationalId) {
		if (nationalId == null || nationalId.isBlank()) {
			return;
		}
		patientRepository.findByNationalId(nationalId)
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new DuplicateResourceException("Patient nationalId already exists: " + nationalId);
				});
	}
}
