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
import com.hospital.hospital.doctor.model.DoctorSpecialty;
import com.hospital.hospital.doctor.model.Specialty;
import com.hospital.hospital.doctor.dto.UpdateDoctorRequest;
import com.hospital.hospital.doctor.mapper.DoctorMapper;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorSpecialtyRepository;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.doctor.repository.SpecialtyRepository;

@Service
public class DoctorServiceImpl implements DoctorService {

	private final DoctorRepository doctorRepository;
	private final DepartmentRepository departmentRepository;
	private final DoctorMapper doctorMapper;
	private final SpecialtyRepository specialtyRepository;
	private final DoctorSpecialtyRepository doctorSpecialtyRepository;

	public DoctorServiceImpl(DoctorRepository doctorRepository, DepartmentRepository departmentRepository,
			DoctorMapper doctorMapper, SpecialtyRepository specialtyRepository,
			DoctorSpecialtyRepository doctorSpecialtyRepository) {
		this.doctorRepository = doctorRepository;
		this.departmentRepository = departmentRepository;
		this.doctorMapper = doctorMapper;
		this.specialtyRepository = specialtyRepository;
		this.doctorSpecialtyRepository = doctorSpecialtyRepository;
	}

	@Override
	@Transactional
	public DoctorResponse create(CreateDoctorRequest request) {
		Doctor doctor = doctorMapper.toEntity(request);
		doctor.setDepartment(getDepartment(request.getDepartmentId()));
		Doctor savedDoctor = doctorRepository.save(doctor);
		syncPrimarySpecialty(savedDoctor, request.getSpecialization());
		return doctorMapper.toResponse(savedDoctor);
	}

	@Override
	@Transactional
	public DoctorResponse update(UUID id, UpdateDoctorRequest request) {
		Doctor doctor = getDoctor(id);
		doctorMapper.updateEntity(request, doctor);
		doctor.setDepartment(getDepartment(request.getDepartmentId()));
		Doctor savedDoctor = doctorRepository.save(doctor);
		syncPrimarySpecialty(savedDoctor, request.getSpecialization());
		return doctorMapper.toResponse(savedDoctor);
	}

	@Override
	@Transactional(readOnly = true)
	public DoctorResponse getById(UUID id) {
		return doctorMapper.toResponse(getDoctor(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DoctorResponse> getAll(Pageable pageable) {
		return doctorRepository.findAllByActiveTrue(pageable).map(doctorMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DoctorResponse> getAllByDepartment(UUID departmentId, Pageable pageable) {
		getDepartment(departmentId);
		return doctorRepository.findAllByDepartmentIdAndActiveTrue(departmentId, pageable).map(doctorMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<DoctorResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String value = keyword.trim();
		return doctorRepository
				.findAllByActiveTrueAndFirstNameContainingIgnoreCaseOrActiveTrueAndLastNameContainingIgnoreCaseOrActiveTrueAndSpecializationContainingIgnoreCase(
						value, value, value, pageable)
				.map(doctorMapper::toResponse);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		Doctor doctor = getDoctor(id);
		doctor.setActive(false);
		doctorRepository.save(doctor);
	}

	private Doctor getDoctor(UUID id) {
		return doctorRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
	}

	private Department getDepartment(UUID id) {
		return departmentRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
	}

	private void syncPrimarySpecialty(Doctor doctor, String specialization) {
		String normalizedSpecialization = normalizeSpecialization(specialization);
		doctorSpecialtyRepository.deleteAllByDoctorId(doctor.getId());
		if (normalizedSpecialization == null) {
			return;
		}
		Specialty specialty = specialtyRepository.findByNameIgnoreCase(normalizedSpecialization)
				.orElseGet(() -> specialtyRepository.save(createSpecialty(normalizedSpecialization)));
		DoctorSpecialty doctorSpecialty = new DoctorSpecialty();
		doctorSpecialty.setDoctor(doctor);
		doctorSpecialty.setSpecialty(specialty);
		doctorSpecialty.setPrimary(true);
		doctorSpecialtyRepository.save(doctorSpecialty);
	}

	private Specialty createSpecialty(String name) {
		Specialty specialty = new Specialty();
		specialty.setName(name);
		specialty.setCode(toCode(name));
		specialty.setDescription(name + " specialty");
		specialty.setActive(true);
		return specialty;
	}

	private String normalizeSpecialization(String specialization) {
		if (specialization == null) {
			return null;
		}
		String normalized = specialization.trim();
		return normalized.isEmpty() ? null : normalized;
	}

	private String toCode(String name) {
		return name.trim().replaceAll("\\s+", "_").toUpperCase();
	}
}
