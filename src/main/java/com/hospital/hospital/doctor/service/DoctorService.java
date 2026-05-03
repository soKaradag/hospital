package com.hospital.hospital.doctor.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.doctor.dto.CreateDoctorRequest;
import com.hospital.hospital.doctor.dto.DoctorResponse;
import com.hospital.hospital.doctor.dto.UpdateDoctorRequest;

public interface DoctorService {

	DoctorResponse create(CreateDoctorRequest request);

	DoctorResponse update(UUID id, UpdateDoctorRequest request);

	DoctorResponse getById(UUID id);

	Page<DoctorResponse> getAll(Pageable pageable);

	Page<DoctorResponse> getAllByDepartment(UUID departmentId, Pageable pageable);

	Page<DoctorResponse> search(String keyword, Pageable pageable);

	void delete(UUID id);
}
