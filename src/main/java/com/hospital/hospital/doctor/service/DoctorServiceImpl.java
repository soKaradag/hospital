package com.hospital.hospital.doctor.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.department.model.Department;
import com.hospital.hospital.department.repository.DepartmentRepository;
import com.hospital.hospital.doctor.dto.CreateDoctorRequest;
import com.hospital.hospital.doctor.dto.DoctorResponse;
import com.hospital.hospital.doctor.dto.UpdateDoctorRequest;
import com.hospital.hospital.doctor.mapper.DoctorMapper;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;

@Service
public class DoctorServiceImpl implements DoctorService {

	private final DoctorRepository doctorRepository;
	private final DepartmentRepository departmentRepository;
	private final DoctorMapper doctorMapper;

	public DoctorServiceImpl(DoctorRepository doctorRepository, DepartmentRepository departmentRepository,
			DoctorMapper doctorMapper) {
		this.doctorRepository = doctorRepository;
		this.departmentRepository = departmentRepository;
		this.doctorMapper = doctorMapper;
	}

	@Override
	@Transactional
	public DoctorResponse create(CreateDoctorRequest request) {
		Doctor doctor = doctorMapper.toEntity(request);
		doctor.setDepartment(getDepartment(request.getDepartmentId()));
		return doctorMapper.toResponse(doctorRepository.save(doctor));
	}

	@Override
	@Transactional
	public DoctorResponse update(UUID id, UpdateDoctorRequest request) {
		Doctor doctor = getDoctor(id);
		doctorMapper.updateEntity(request, doctor);
		doctor.setDepartment(getDepartment(request.getDepartmentId()));
		return doctorMapper.toResponse(doctorRepository.save(doctor));
	}

	@Override
	@Transactional(readOnly = true)
	public DoctorResponse getById(UUID id) {
		return doctorMapper.toResponse(getDoctor(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DoctorResponse> getAll(Pageable pageable) {
		return doctorRepository.findAll(pageable).map(doctorMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DoctorResponse> getAllByDepartment(UUID departmentId, Pageable pageable) {
		getDepartment(departmentId);
		return doctorRepository.findAllByDepartmentId(departmentId, pageable).map(doctorMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DoctorResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String value = keyword.trim();
		return doctorRepository
				.findAllByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrSpecializationContainingIgnoreCase(
						value, value, value, pageable)
				.map(doctorMapper::toResponse);
	}

	private Doctor getDoctor(UUID id) {
		return doctorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
	}

	private Department getDepartment(UUID id) {
		return departmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
	}
}
