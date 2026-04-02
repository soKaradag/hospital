package com.hospital.hospital.appointment.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.appointment.dto.AppointmentResponse;
import com.hospital.hospital.appointment.dto.CreateAppointmentRequest;
import com.hospital.hospital.appointment.dto.UpdateAppointmentRequest;

// Service interface, ne yapılacağını tanımlar.
public interface AppointmentService {

	AppointmentResponse create(CreateAppointmentRequest request);

	AppointmentResponse createWithProcedure(CreateAppointmentRequest request);

	AppointmentResponse update(UUID id, UpdateAppointmentRequest request);

	AppointmentResponse getById(UUID id);

	Page<AppointmentResponse> getAll(Pageable pageable);

	Page<AppointmentResponse> getAllByPatient(UUID patientId, Pageable pageable);

	Page<AppointmentResponse> getAllByDoctor(UUID doctorId, Pageable pageable);

	Page<AppointmentResponse> getAllByDateRange(Instant startInclusive, Instant endInclusive, Pageable pageable);

	Page<AppointmentResponse> search(String keyword, Pageable pageable);
}
