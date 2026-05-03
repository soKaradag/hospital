package com.hospital.hospital.department.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.department.dto.CreateDepartmentRequest;
import com.hospital.hospital.department.dto.DepartmentResponse;
import com.hospital.hospital.department.dto.UpdateDepartmentRequest;

public interface DepartmentService {

	DepartmentResponse create(CreateDepartmentRequest request);

	DepartmentResponse update(UUID id, UpdateDepartmentRequest request);

	DepartmentResponse getById(UUID id);

	Page<DepartmentResponse> getAll(Pageable pageable);

	Page<DepartmentResponse> search(String keyword, Pageable pageable);

	void delete(UUID id);
}
