package com.hospital.hospital.encounter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import com.hospital.hospital.appointment.model.Appointment;
import com.hospital.hospital.appointment.repository.AppointmentRepository;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.encounter.dto.CreateEncounterRequest;
import com.hospital.hospital.encounter.dto.EncounterResponse;
import com.hospital.hospital.encounter.mapper.EncounterMapper;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class EncounterServiceImplTest {

	@Mock
	private EncounterRepository encounterRepository;

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private EncounterMapper encounterMapper;

	@InjectMocks
	private EncounterServiceImpl encounterService;

	@Test
	void createShouldSaveEncounterWhenRelationsAreConsistent() {
		UUID appointmentId = UUID.randomUUID();
		UUID patientId = UUID.randomUUID();
		UUID doctorId = UUID.randomUUID();

		Patient patient = new Patient();
		patient.setId(patientId);
		Doctor doctor = new Doctor();
		doctor.setId(doctorId);

		Appointment appointment = new Appointment();
		appointment.setId(appointmentId);
		appointment.setPatient(patient);
		appointment.setDoctor(doctor);

		CreateEncounterRequest request = new CreateEncounterRequest();
		request.setAppointmentId(appointmentId);
		request.setPatientId(patientId);
		request.setDoctorId(doctorId);
		request.setEncounterDateTime(Instant.parse("2026-03-27T12:00:00Z"));

		Encounter mappedEncounter = new Encounter();
		Encounter savedEncounter = new Encounter();
		EncounterResponse response = new EncounterResponse();

		when(encounterMapper.toEntity(request)).thenReturn(mappedEncounter);
		when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
		when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
		when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
		when(encounterRepository.save(mappedEncounter)).thenReturn(savedEncounter);
		when(encounterMapper.toResponse(savedEncounter)).thenReturn(response);

		EncounterResponse actual = encounterService.create(request);

		assertNotNull(actual);
		assertEquals(appointment, mappedEncounter.getAppointment());
		assertEquals(patient, mappedEncounter.getPatient());
		assertEquals(doctor, mappedEncounter.getDoctor());
	}

	@Test
	void createShouldThrowWhenAppointmentPatientDoesNotMatchEncounterPatient() {
		UUID appointmentId = UUID.randomUUID();
		UUID requestPatientId = UUID.randomUUID();
		UUID appointmentPatientId = UUID.randomUUID();
		UUID doctorId = UUID.randomUUID();

		Patient requestPatient = new Patient();
		requestPatient.setId(requestPatientId);
		Patient appointmentPatient = new Patient();
		appointmentPatient.setId(appointmentPatientId);
		Doctor doctor = new Doctor();
		doctor.setId(doctorId);

		Appointment appointment = new Appointment();
		appointment.setPatient(appointmentPatient);
		appointment.setDoctor(doctor);

		CreateEncounterRequest request = new CreateEncounterRequest();
		request.setAppointmentId(appointmentId);
		request.setPatientId(requestPatientId);
		request.setDoctorId(doctorId);

		when(encounterMapper.toEntity(request)).thenReturn(new Encounter());
		when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
		when(patientRepository.findById(requestPatientId)).thenReturn(Optional.of(requestPatient));
		when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

		assertThrows(BusinessRuleViolationException.class, () -> encounterService.create(request));
	}

	@Test
	void createShouldThrowWhenAppointmentDoctorDoesNotMatchEncounterDoctor() {
		UUID appointmentId = UUID.randomUUID();
		UUID patientId = UUID.randomUUID();
		UUID requestDoctorId = UUID.randomUUID();
		UUID appointmentDoctorId = UUID.randomUUID();

		Patient patient = new Patient();
		patient.setId(patientId);
		Doctor requestDoctor = new Doctor();
		requestDoctor.setId(requestDoctorId);
		Doctor appointmentDoctor = new Doctor();
		appointmentDoctor.setId(appointmentDoctorId);

		Appointment appointment = new Appointment();
		appointment.setPatient(patient);
		appointment.setDoctor(appointmentDoctor);

		CreateEncounterRequest request = new CreateEncounterRequest();
		request.setAppointmentId(appointmentId);
		request.setPatientId(patientId);
		request.setDoctorId(requestDoctorId);

		when(encounterMapper.toEntity(request)).thenReturn(new Encounter());
		when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
		when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
		when(doctorRepository.findById(requestDoctorId)).thenReturn(Optional.of(requestDoctor));

		assertThrows(BusinessRuleViolationException.class, () -> encounterService.create(request));
	}

	@Test
	void searchShouldReturnPagedEncounters() {
		String keyword = "pain";
		PageRequest pageable = PageRequest.of(0, 10);
		Encounter encounter = new Encounter();
		Page<Encounter> page = new PageImpl<>(List.of(encounter), pageable, 1);

		when(encounterRepository
				.findAllByComplaintContainingIgnoreCaseOrDiagnosisNoteContainingIgnoreCaseOrTreatmentNoteContainingIgnoreCase(
						keyword, keyword, keyword, pageable))
				.thenReturn(page);
		when(encounterMapper.toResponse(encounter)).thenReturn(new EncounterResponse());

		Page<EncounterResponse> result = encounterService.search(keyword, pageable);

		assertEquals(1, result.getTotalElements());
	}
}
