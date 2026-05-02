package com.hospital.hospital.department.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.department.dto.CreateDepartmentRequest;
import com.hospital.hospital.department.dto.DepartmentResponse;
import com.hospital.hospital.department.dto.UpdateDepartmentRequest;
import com.hospital.hospital.department.model.Department;

// Department entity ve dto dönüşümlerini manuel olarak yönetir.
@Component
public class DepartmentMapper {

	public Department toEntity(CreateDepartmentRequest request) {
		if (request == null) {
			return null;
		}
		return new Department(request.getName(), request.getDescription());
	}

	public void updateEntity(UpdateDepartmentRequest request, Department department) {
		if (request == null || department == null) {
			return;
		}
		department.setName(request.getName());
		department.setDescription(request.getDescription());
	}

	public DepartmentResponse toResponse(Department department) {
		return toResponse(department, 0L, 0L);
	}

	public DepartmentResponse toResponse(Department department, long roomCount, long serviceCount) {
		if (department == null) {
			return null;
		}
		DepartmentResponse response = new DepartmentResponse();
		response.setId(department.getId());
		response.setName(department.getName());
		response.setDescription(department.getDescription());
		response.setRoomCount(roomCount);
		response.setServiceCount(serviceCount);
		response.setCreatedAt(department.getCreatedAt());
		response.setUpdatedAt(department.getUpdatedAt());
		return response;
	}
}
