package com.hospital.hospital.patient.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.patient.dto.CreatePatientRequest;
import com.hospital.hospital.patient.dto.PatientResponse;
import com.hospital.hospital.patient.dto.UpdatePatientRequest;
import com.hospital.hospital.patient.mapper.PatientMapper;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientEmergencyContactRepository;
import com.hospital.hospital.patient.repository.PatientInsuranceRepository;
import com.hospital.hospital.patient.repository.PatientRepository;

@Service
public class PatientServiceImpl implements PatientService {

	private final PatientRepository patientRepository;
	private final PatientMapper patientMapper;
	private final PatientEmergencyContactRepository patientEmergencyContactRepository;
	private final PatientInsuranceRepository patientInsuranceRepository;

	public PatientServiceImpl(PatientRepository patientRepository, PatientMapper patientMapper,
			PatientEmergencyContactRepository patientEmergencyContactRepository,
			PatientInsuranceRepository patientInsuranceRepository) {
		this.patientRepository = patientRepository;
		this.patientMapper = patientMapper;
		this.patientEmergencyContactRepository = patientEmergencyContactRepository;
		this.patientInsuranceRepository = patientInsuranceRepository;
	}

	@Override
	@Transactional
	@Audit(action = "CREATE_PATIENT", entity = "PATIENT", description = "Patient creation")
	public PatientResponse create(CreatePatientRequest request) {
		validateNationalIdForCreate(request.getNationalId());
		Patient patient = patientMapper.toEntity(request);
		return toResponse(patientRepository.save(patient));
	}

	@Override
	@Transactional
	@Audit(action = "UPDATE_PATIENT", entity = "PATIENT", description = "Patient update")
	public PatientResponse update(UUID id, UpdatePatientRequest request) {
		Patient patient = getPatient(id);
		validateNationalIdForUpdate(id, request.getNationalId());
		patientMapper.updateEntity(request, patient);
		return toResponse(patientRepository.save(patient));
	}

	@Override
	@Transactional(readOnly = true)
	public PatientResponse getById(UUID id) {
		return toResponse(getPatient(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PatientResponse> getAll(Pageable pageable) {
		return patientRepository.findAll(pageable).map(this::toResponse);
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
				.map(this::toResponse);
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

	private PatientResponse toResponse(Patient patient) {
		long emergencyContactCount = patientEmergencyContactRepository.countByPatientId(patient.getId());
		long insuranceCount = patientInsuranceRepository.countByPatientId(patient.getId());
		return patientMapper.toResponse(patient, emergencyContactCount, insuranceCount);
	}
}
