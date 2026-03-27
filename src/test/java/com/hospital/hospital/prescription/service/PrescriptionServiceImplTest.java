package com.hospital.hospital.prescription.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;
import com.hospital.hospital.prescription.dto.CreatePrescriptionRequest;
import com.hospital.hospital.prescription.mapper.PrescriptionMapper;
import com.hospital.hospital.prescription.model.Prescription;
import com.hospital.hospital.prescription.repository.PrescriptionRepository;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

	@Mock
	private PrescriptionRepository prescriptionRepository;

	@Mock
	private EncounterRepository encounterRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private PrescriptionMapper prescriptionMapper;

	@InjectMocks
	private PrescriptionServiceImpl prescriptionService;

	@Test
	void createShouldThrowWhenPatientDoesNotMatchEncounterPatient() {
		UUID encounterId = UUID.randomUUID();
		UUID patientId = UUID.randomUUID();
		UUID otherPatientId = UUID.randomUUID();
		UUID doctorId = UUID.randomUUID();

		CreatePrescriptionRequest request = new CreatePrescriptionRequest();
		request.setEncounterId(encounterId);
		request.setPatientId(patientId);
		request.setDoctorId(doctorId);
		request.setPrescriptionDate(LocalDate.of(2026, 3, 27));

		Patient encounterPatient = new Patient();
		encounterPatient.setId(otherPatientId);

		Patient requestPatient = new Patient();
		requestPatient.setId(patientId);

		Doctor doctor = new Doctor();
		doctor.setId(doctorId);

		Encounter encounter = new Encounter();
		encounter.setId(encounterId);
		encounter.setPatient(encounterPatient);
		encounter.setDoctor(doctor);

		when(prescriptionMapper.toEntity(request)).thenReturn(new Prescription());
		when(encounterRepository.findById(encounterId)).thenReturn(Optional.of(encounter));
		when(patientRepository.findById(patientId)).thenReturn(Optional.of(requestPatient));
		when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

		assertThrows(BusinessRuleViolationException.class, () -> prescriptionService.create(request));
		verify(prescriptionRepository, never()).save(org.mockito.ArgumentMatchers.any(Prescription.class));
	}
}
