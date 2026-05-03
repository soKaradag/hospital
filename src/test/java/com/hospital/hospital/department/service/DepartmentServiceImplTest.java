package com.hospital.hospital.department.service;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.department.dto.CreateDepartmentRequest;
import com.hospital.hospital.department.dto.DepartmentResponse;
import com.hospital.hospital.department.dto.UpdateDepartmentRequest;
import com.hospital.hospital.department.mapper.DepartmentMapper;
import com.hospital.hospital.department.model.Department;
import com.hospital.hospital.department.repository.DepartmentRepository;
import com.hospital.hospital.department.repository.DepartmentServiceCatalogRepository;
import com.hospital.hospital.department.repository.RoomRepository;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

	@Mock
	private DepartmentRepository departmentRepository;

	@Mock
	private DepartmentMapper departmentMapper;

	@Mock
	private RoomRepository roomRepository;

	@Mock
	private DepartmentServiceCatalogRepository departmentServiceCatalogRepository;

	@InjectMocks
	private DepartmentServiceImpl departmentService;

	@Test
	void createShouldSaveDepartmentWhenNameIsUnique() {
		CreateDepartmentRequest request = new CreateDepartmentRequest();
		request.setName("Cardiology");
		request.setDescription("Heart diseases");

		Department mappedDepartment = new Department();
		Department savedDepartment = new Department();
		savedDepartment.setId(UUID.randomUUID());
		savedDepartment.setName("Cardiology");
		savedDepartment.setCreatedAt(Instant.parse("2026-03-27T10:00:00Z"));
		savedDepartment.setUpdatedAt(Instant.parse("2026-03-27T10:00:00Z"));

		DepartmentResponse response = new DepartmentResponse();
		response.setId(savedDepartment.getId());
		response.setName(savedDepartment.getName());

		when(departmentRepository.existsByName("Cardiology")).thenReturn(false);
		when(departmentMapper.toEntity(request)).thenReturn(mappedDepartment);
		when(departmentRepository.save(mappedDepartment)).thenReturn(savedDepartment);
		when(roomRepository.countByDepartmentId(savedDepartment.getId())).thenReturn(0L);
		when(departmentServiceCatalogRepository.countByDepartmentId(savedDepartment.getId())).thenReturn(0L);
		when(departmentMapper.toResponse(savedDepartment, 0L, 0L)).thenReturn(response);

		DepartmentResponse actual = departmentService.create(request);

		assertNotNull(actual);
		assertEquals("Cardiology", actual.getName());
		verify(departmentRepository).save(mappedDepartment);
	}

	@Test
	void createShouldThrowWhenDepartmentNameAlreadyExists() {
		CreateDepartmentRequest request = new CreateDepartmentRequest();
		request.setName("Cardiology");

		when(departmentRepository.existsByName("Cardiology")).thenReturn(true);

		assertThrows(DuplicateResourceException.class, () -> departmentService.create(request));
		verify(departmentMapper, never()).toEntity(any(CreateDepartmentRequest.class));
	}

	@Test
	void updateShouldPersistChangesWhenNameBelongsToCurrentDepartment() {
		UUID id = UUID.randomUUID();
		UpdateDepartmentRequest request = new UpdateDepartmentRequest();
		request.setName("Cardiology");
		request.setDescription("Updated");

		Department existing = new Department();
		existing.setId(id);
		existing.setName("Cardiology");

		DepartmentResponse response = new DepartmentResponse();
		response.setId(id);
		response.setDescription("Updated");

		when(departmentRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(existing));
		when(departmentRepository.findByName("Cardiology")).thenReturn(Optional.of(existing));
		when(departmentRepository.save(existing)).thenReturn(existing);
		when(roomRepository.countByDepartmentId(id)).thenReturn(0L);
		when(departmentServiceCatalogRepository.countByDepartmentId(id)).thenReturn(0L);
		when(departmentMapper.toResponse(existing, 0L, 0L)).thenReturn(response);

		DepartmentResponse actual = departmentService.update(id, request);

		assertEquals("Updated", actual.getDescription());
		verify(departmentMapper).updateEntity(request, existing);
		verify(departmentRepository).save(existing);
	}

	@Test
	void searchShouldFallbackToGetAllWhenKeywordIsBlank() {
		PageRequest pageable = PageRequest.of(0, 10);
		Department department = new Department();
		Page<Department> page = new PageImpl<>(java.util.List.of(department), pageable, 1);

		when(departmentRepository.findAllByActiveTrue(pageable)).thenReturn(page);
		when(roomRepository.countByDepartmentId(null)).thenReturn(0L);
		when(departmentServiceCatalogRepository.countByDepartmentId(null)).thenReturn(0L);
		when(departmentMapper.toResponse(department, 0L, 0L)).thenReturn(new DepartmentResponse());

		Page<DepartmentResponse> result = departmentService.search("   ", pageable);

		assertEquals(1, result.getTotalElements());
		verify(departmentRepository).findAllByActiveTrue(pageable);
	}

	@Test
	void deleteShouldDeactivateDepartment() {
		UUID id = UUID.randomUUID();
		Department department = new Department();
		department.setId(id);
		department.setActive(true);

		when(departmentRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(department));

		departmentService.delete(id);

		verify(departmentRepository).save(department);
	}
}
