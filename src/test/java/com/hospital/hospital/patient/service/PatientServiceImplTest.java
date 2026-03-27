package com.hospital.hospital.patient.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.patient.dto.CreatePatientRequest;
import com.hospital.hospital.patient.mapper.PatientMapper;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private PatientMapper patientMapper;

	@InjectMocks
	private PatientServiceImpl patientService;

	@Test
	void createShouldThrowWhenNationalIdAlreadyExists() {
		CreatePatientRequest request = new CreatePatientRequest();
		request.setNationalId("12345678901");

		when(patientRepository.existsByNationalId("12345678901")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> patientService.create(request));
		verify(patientMapper, never()).toEntity(any(CreatePatientRequest.class));
		verify(patientRepository, never()).save(any(Patient.class));
	}
}
