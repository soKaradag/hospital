package com.hospital.hospital.doctor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.auth.model.User;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.department.model.Department;
import com.hospital.hospital.department.repository.DepartmentRepository;
import com.hospital.hospital.auth.repository.UserRepository;
import com.hospital.hospital.auth.repository.UserRoleRepository;
import com.hospital.hospital.doctor.dto.CreateDoctorRequest;
import com.hospital.hospital.doctor.dto.DoctorResponse;
import com.hospital.hospital.doctor.mapper.DoctorMapper;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorSpecialtyRepository;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.doctor.repository.SpecialtyRepository;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private DepartmentRepository departmentRepository;

	@Mock
	private DoctorMapper doctorMapper;

	@Mock
	private SpecialtyRepository specialtyRepository;

	@Mock
	private DoctorSpecialtyRepository doctorSpecialtyRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserRoleRepository userRoleRepository;

	@InjectMocks
	private DoctorServiceImpl doctorService;

	@Test
	void createShouldSaveDoctorWhenDepartmentExists() {
		UUID departmentId = UUID.randomUUID();
		CreateDoctorRequest request = new CreateDoctorRequest();
		request.setDepartmentId(departmentId);
		request.setFirstName("Alice");

		Department department = new Department();
		department.setId(departmentId);
		department.setName("Cardiology");

		Doctor mappedDoctor = new Doctor();
		Doctor savedDoctor = new Doctor();
		savedDoctor.setDepartment(department);

		DoctorResponse response = new DoctorResponse();
		response.setDepartmentId(departmentId);

		when(doctorMapper.toEntity(request)).thenReturn(mappedDoctor);
		when(departmentRepository.findByIdAndActiveTrue(departmentId)).thenReturn(Optional.of(department));
		when(doctorRepository.save(mappedDoctor)).thenReturn(savedDoctor);
		when(doctorMapper.toResponse(savedDoctor)).thenReturn(response);

		DoctorResponse actual = doctorService.create(request);

		assertNotNull(actual);
		assertEquals(department, mappedDoctor.getDepartment());
		verify(doctorRepository).save(mappedDoctor);
	}

	@Test
	void createShouldThrowWhenDepartmentDoesNotExist() {
		UUID departmentId = UUID.randomUUID();
		CreateDoctorRequest request = new CreateDoctorRequest();
		request.setDepartmentId(departmentId);

		when(doctorMapper.toEntity(request)).thenReturn(new Doctor());
		when(departmentRepository.findByIdAndActiveTrue(departmentId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> doctorService.create(request));
	}

	@Test
	void getAllByDepartmentShouldReturnPagedDoctors() {
		UUID departmentId = UUID.randomUUID();
		PageRequest pageable = PageRequest.of(0, 10);
		Department department = new Department();
		department.setId(departmentId);

		Doctor doctor = new Doctor();
		Page<Doctor> page = new PageImpl<>(List.of(doctor), pageable, 1);

		when(departmentRepository.findByIdAndActiveTrue(departmentId)).thenReturn(Optional.of(department));
		when(doctorRepository.findAllByDepartmentIdAndActiveTrue(departmentId, pageable)).thenReturn(page);
		when(doctorMapper.toResponse(doctor)).thenReturn(new DoctorResponse());

		Page<DoctorResponse> result = doctorService.getAllByDepartment(departmentId, pageable);

		assertEquals(1, result.getTotalElements());
	}

	@Test
	void searchShouldReturnPagedDoctors() {
		String keyword = "card";
		PageRequest pageable = PageRequest.of(0, 10);
		Doctor doctor = new Doctor();
		Page<Doctor> page = new PageImpl<>(List.of(doctor), pageable, 1);

		when(doctorRepository
				.findAllByActiveTrueAndFirstNameContainingIgnoreCaseOrActiveTrueAndLastNameContainingIgnoreCaseOrActiveTrueAndSpecializationContainingIgnoreCase(
						keyword, keyword, keyword, pageable))
				.thenReturn(page);
		when(doctorMapper.toResponse(doctor)).thenReturn(new DoctorResponse());

		Page<DoctorResponse> result = doctorService.search(keyword, pageable);

		assertEquals(1, result.getTotalElements());
	}

	@Test
	void deleteShouldDeactivateDoctor() {
		UUID id = UUID.randomUUID();
		Doctor doctor = new Doctor();
		doctor.setId(id);
		doctor.setActive(true);

		when(doctorRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(doctor));

		doctorService.delete(id);

		verify(doctorRepository).save(doctor);
	}

	@Test
	void createShouldLinkDoctorUserWhenAssignedUserHasDoctorRole() {
		UUID departmentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		CreateDoctorRequest request = new CreateDoctorRequest();
		request.setDepartmentId(departmentId);
		request.setUserId(userId);

		Department department = new Department();
		department.setId(departmentId);

		User user = new User("doctor.user", "hash", Role.DOCTOR);
		user.setId(userId);

		Doctor mappedDoctor = new Doctor();
		Doctor savedDoctor = new Doctor();
		savedDoctor.setDepartment(department);
		savedDoctor.setUser(user);

		when(doctorMapper.toEntity(request)).thenReturn(mappedDoctor);
		when(departmentRepository.findByIdAndActiveTrue(departmentId)).thenReturn(Optional.of(department));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRoleRepository.hasRoleCode(userId, Role.DOCTOR.name())).thenReturn(true);
		when(doctorRepository.existsByUser_IdAndActiveTrue(userId)).thenReturn(false);
		when(doctorRepository.save(mappedDoctor)).thenReturn(savedDoctor);
		when(doctorMapper.toResponse(savedDoctor)).thenReturn(new DoctorResponse());

		doctorService.create(request);

		assertEquals(user, mappedDoctor.getUser());
	}

	@Test
	void createShouldRejectLinkedUsersWithoutDoctorRole() {
		UUID departmentId = UUID.randomUUID();
		UUID userId = UUID.randomUUID();
		CreateDoctorRequest request = new CreateDoctorRequest();
		request.setDepartmentId(departmentId);
		request.setUserId(userId);

		Department department = new Department();
		department.setId(departmentId);

		User user = new User("admin.user", "hash", Role.ADMIN);
		user.setId(userId);

		when(doctorMapper.toEntity(request)).thenReturn(new Doctor());
		when(departmentRepository.findByIdAndActiveTrue(departmentId)).thenReturn(Optional.of(department));
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userRoleRepository.hasRoleCode(userId, Role.DOCTOR.name())).thenReturn(false);

		BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class,
				() -> doctorService.create(request));

		assertEquals("Linked user must have DOCTOR role", exception.getMessage());
	}
}
