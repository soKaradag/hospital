package com.hospital.hospital.patient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.patient.dto.CreatePatientRequest;
import com.hospital.hospital.patient.dto.PatientResponse;
import com.hospital.hospital.patient.dto.UpdatePatientRequest;
import com.hospital.hospital.patient.mapper.PatientMapper;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplAdditionalTest {

	@Mock
	private PatientRepository patientRepository;
	@Mock
	private PatientMapper patientMapper;
	@InjectMocks
	private PatientServiceImpl patientService;

	@Test
	void createShouldSavePatientWhenNationalIdIsUnique() {
		CreatePatientRequest request = new CreatePatientRequest();
		request.setNationalId("123");

		Patient patient = new Patient();
		Patient saved = new Patient();
		saved.setId(UUID.randomUUID());
		saved.setCreatedAt(Instant.parse("2026-03-27T10:00:00Z"));
		PatientResponse response = new PatientResponse();
		response.setId(saved.getId());

		when(patientRepository.existsByNationalId("123")).thenReturn(false);
		when(patientMapper.toEntity(request)).thenReturn(patient);
		when(patientRepository.save(patient)).thenReturn(saved);
		when(patientMapper.toResponse(saved)).thenReturn(response);

		PatientResponse actual = patientService.create(request);
		assertNotNull(actual);
		verify(patientRepository).save(patient);
	}

	@Test
	void updateShouldThrowWhenNationalIdBelongsToAnotherPatient() {
		UUID id = UUID.randomUUID();
		UpdatePatientRequest request = new UpdatePatientRequest();
		request.setNationalId("123");

		Patient current = new Patient();
		current.setId(id);
		Patient other = new Patient();
		other.setId(UUID.randomUUID());

		when(patientRepository.findById(id)).thenReturn(Optional.of(current));
		when(patientRepository.findByNationalId("123")).thenReturn(Optional.of(other));

		assertThrows(DuplicateResourceException.class, () -> patientService.update(id, request));
	}

	@Test
	void searchShouldFallbackToGetAllWhenKeywordIsBlank() {
		PageRequest pageable = PageRequest.of(0, 10);
		Patient patient = new Patient();
		Page<Patient> page = new PageImpl<>(List.of(patient), pageable, 1);

		when(patientRepository.findAll(pageable)).thenReturn(page);
		when(patientMapper.toResponse(patient)).thenReturn(new PatientResponse());

		Page<PatientResponse> result = patientService.search(" ", pageable);
		assertEquals(1, result.getTotalElements());
	}
}
