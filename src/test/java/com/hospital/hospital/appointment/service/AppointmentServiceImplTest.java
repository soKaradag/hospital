package com.hospital.hospital.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.appointment.dto.AppointmentResponse;
import com.hospital.hospital.appointment.dto.CreateAppointmentRequest;
import com.hospital.hospital.appointment.mapper.AppointmentMapper;
import com.hospital.hospital.appointment.model.Appointment;
import com.hospital.hospital.appointment.model.AppointmentStatus;
import com.hospital.hospital.appointment.repository.AppointmentRepository;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private AppointmentMapper appointmentMapper;

	@InjectMocks
	private AppointmentServiceImpl appointmentService;

	@Test
	void createShouldThrowWhenAppointmentDateIsInThePast() {
		CreateAppointmentRequest request = new CreateAppointmentRequest();
		request.setAppointmentDateTime(Instant.parse("2000-01-01T10:00:00Z"));

		assertThrows(BusinessRuleViolationException.class, () -> appointmentService.create(request));
		verify(appointmentRepository, never()).save(any(Appointment.class));
	}

	@Test
	void createShouldSaveAppointmentWhenRequestIsValid() {
		UUID patientId = UUID.randomUUID();
		UUID doctorId = UUID.randomUUID();

		CreateAppointmentRequest request = new CreateAppointmentRequest();
		request.setPatientId(patientId);
		request.setDoctorId(doctorId);
		request.setAppointmentDateTime(Instant.parse("2099-01-01T10:00:00Z"));
		request.setStatus(AppointmentStatus.SCHEDULED);
		request.setNotes("Routine control");

		Patient patient = new Patient();
		patient.setId(patientId);

		Doctor doctor = new Doctor();
		doctor.setId(doctorId);

		Appointment mappedAppointment = new Appointment();
		Appointment savedAppointment = new Appointment();
		savedAppointment.setPatient(patient);
		savedAppointment.setDoctor(doctor);
		savedAppointment.setAppointmentDateTime(request.getAppointmentDateTime());
		savedAppointment.setStatus(request.getStatus());

		AppointmentResponse response = new AppointmentResponse();
		response.setPatientId(patientId);
		response.setDoctorId(doctorId);

		when(appointmentMapper.toEntity(request)).thenReturn(mappedAppointment);
		when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
		when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
		when(appointmentRepository.save(mappedAppointment)).thenReturn(savedAppointment);
		when(appointmentMapper.toResponse(savedAppointment)).thenReturn(response);

		AppointmentResponse actual = appointmentService.create(request);

		assertNotNull(actual);
		assertEquals(patientId, actual.getPatientId());
		assertEquals(doctorId, actual.getDoctorId());
		assertEquals(patient, mappedAppointment.getPatient());
		assertEquals(doctor, mappedAppointment.getDoctor());
		verify(appointmentRepository).save(mappedAppointment);
	}

	@Test
	void createShouldThrowWhenPatientDoesNotExist() {
		UUID patientId = UUID.randomUUID();
		UUID doctorId = UUID.randomUUID();

		CreateAppointmentRequest request = new CreateAppointmentRequest();
		request.setPatientId(patientId);
		request.setDoctorId(doctorId);
		request.setAppointmentDateTime(Instant.parse("2099-01-01T10:00:00Z"));
		request.setStatus(AppointmentStatus.SCHEDULED);

		when(appointmentMapper.toEntity(request)).thenReturn(new Appointment());
		when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(request));
		verify(appointmentRepository, never()).save(any(Appointment.class));
	}

	@Test
	void createShouldThrowWhenDoctorDoesNotExist() {
		UUID patientId = UUID.randomUUID();
		UUID doctorId = UUID.randomUUID();
		Patient patient = new Patient();
		patient.setId(patientId);

		CreateAppointmentRequest request = new CreateAppointmentRequest();
		request.setPatientId(patientId);
		request.setDoctorId(doctorId);
		request.setAppointmentDateTime(Instant.parse("2099-01-01T10:00:00Z"));
		request.setStatus(AppointmentStatus.SCHEDULED);

		when(appointmentMapper.toEntity(request)).thenReturn(new Appointment());
		when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
		when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(request));
		verify(appointmentRepository, never()).save(any(Appointment.class));
	}
}
