package com.hospital.hospital.doctor.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.common.mapper.CommonValueObjectMapper;
import com.hospital.hospital.doctor.dto.CreateDoctorRequest;
import com.hospital.hospital.doctor.dto.DoctorResponse;
import com.hospital.hospital.doctor.dto.UpdateDoctorRequest;
import com.hospital.hospital.doctor.model.Doctor;

// Doctor entity ve dto dönüşümlerini manuel olarak yönetir.
@Component
public class DoctorMapper {

	private final CommonValueObjectMapper commonValueObjectMapper;

	public DoctorMapper(CommonValueObjectMapper commonValueObjectMapper) {
		this.commonValueObjectMapper = commonValueObjectMapper;
	}

	public Doctor toEntity(CreateDoctorRequest request) {
		if (request == null) {
			return null;
		}
		Doctor doctor = new Doctor();
		doctor.setFirstName(request.getFirstName());
		doctor.setLastName(request.getLastName());
		doctor.setSpecialization(request.getSpecialization());
		doctor.setContact(commonValueObjectMapper.toEntity(request.getContact()));
		return doctor;
	}

	public void updateEntity(UpdateDoctorRequest request, Doctor doctor) {
		if (request == null || doctor == null) {
			return;
		}
		doctor.setFirstName(request.getFirstName());
		doctor.setLastName(request.getLastName());
		doctor.setSpecialization(request.getSpecialization());
		doctor.setContact(commonValueObjectMapper.toEntity(request.getContact()));
	}

	public DoctorResponse toResponse(Doctor doctor) {
		if (doctor == null) {
			return null;
		}
		DoctorResponse response = new DoctorResponse();
		response.setId(doctor.getId());
		response.setFirstName(doctor.getFirstName());
		response.setLastName(doctor.getLastName());
		response.setSpecialization(doctor.getSpecialization());
		response.setContact(commonValueObjectMapper.toDto(doctor.getContact()));
		if (doctor.getDepartment() != null) {
			response.setDepartmentId(doctor.getDepartment().getId());
			response.setDepartmentName(doctor.getDepartment().getName());
		}
		if (doctor.getUser() != null) {
			response.setUserId(doctor.getUser().getId());
			response.setUsername(doctor.getUser().getUsername());
		}
		response.setCreatedAt(doctor.getCreatedAt());
		response.setUpdatedAt(doctor.getUpdatedAt());
		return response;
	}
}
