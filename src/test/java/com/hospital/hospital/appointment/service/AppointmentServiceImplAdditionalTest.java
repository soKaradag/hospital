package com.hospital.hospital.appointment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.hospital.hospital.appointment.dto.AppointmentResponse;
import com.hospital.hospital.appointment.mapper.AppointmentMapper;
import com.hospital.hospital.appointment.model.Appointment;
import com.hospital.hospital.appointment.repository.AppointmentRepository;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.patient.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplAdditionalTest {

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
	void searchShouldReturnPagedAppointmentsByNotes() {
		String keyword = "control";
		PageRequest pageable = PageRequest.of(0, 10);
		Appointment appointment = new Appointment();
		Page<Appointment> page = new PageImpl<>(List.of(appointment), pageable, 1);

		when(appointmentRepository.findAllByNotesContainingIgnoreCase(keyword, pageable)).thenReturn(page);
		when(appointmentMapper.toResponse(appointment)).thenReturn(new AppointmentResponse());

		Page<AppointmentResponse> result = appointmentService.search(keyword, pageable);

		assertEquals(1, result.getTotalElements());
	}

	@Test
	void getAllByDateRangeShouldReturnPagedAppointments() {
		PageRequest pageable = PageRequest.of(0, 10);
		Appointment appointment = new Appointment();
		Page<Appointment> page = new PageImpl<>(List.of(appointment), pageable, 1);
		Instant start = Instant.parse("2026-03-27T10:00:00Z");
		Instant end = Instant.parse("2026-03-27T12:00:00Z");

		when(appointmentRepository.findAllByAppointmentDateTimeBetween(start, end, pageable)).thenReturn(page);
		when(appointmentMapper.toResponse(appointment)).thenReturn(new AppointmentResponse());

		Page<AppointmentResponse> result = appointmentService.getAllByDateRange(start, end, pageable);

		assertEquals(1, result.getTotalElements());
	}
}
