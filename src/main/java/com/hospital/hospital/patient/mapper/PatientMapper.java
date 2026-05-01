package com.hospital.hospital.patient.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.common.mapper.CommonValueObjectMapper;
import com.hospital.hospital.patient.dto.CreatePatientRequest;
import com.hospital.hospital.patient.dto.PatientResponse;
import com.hospital.hospital.patient.dto.UpdatePatientRequest;
import com.hospital.hospital.patient.model.Patient;

// Patient entity ve dto dönüşümlerini manuel olarak yönetir.
@Component
public class PatientMapper {

	private final CommonValueObjectMapper commonValueObjectMapper;

	public PatientMapper(CommonValueObjectMapper commonValueObjectMapper) {
		this.commonValueObjectMapper = commonValueObjectMapper;
	}

	public Patient toEntity(CreatePatientRequest request) {
		if (request == null) {
			return null;
		}
		Patient patient = new Patient();
		patient.setFirstName(request.getFirstName());
		patient.setLastName(request.getLastName());
		patient.setNationalId(request.getNationalId());
		patient.setBirthDate(request.getBirthDate());
		patient.setGender(request.getGender());
		patient.setContact(commonValueObjectMapper.toEntity(request.getContact()));
		patient.setAddress(commonValueObjectMapper.toEntity(request.getAddress()));
		return patient;
	}

	public void updateEntity(UpdatePatientRequest request, Patient patient) {
		if (request == null || patient == null) {
			return;
		}
		patient.setFirstName(request.getFirstName());
		patient.setLastName(request.getLastName());
		patient.setNationalId(request.getNationalId());
		patient.setBirthDate(request.getBirthDate());
		patient.setGender(request.getGender());
		patient.setContact(commonValueObjectMapper.toEntity(request.getContact()));
		patient.setAddress(commonValueObjectMapper.toEntity(request.getAddress()));
	}

	public PatientResponse toResponse(Patient patient) {
		return toResponse(patient, 0L, 0L);
	}

	public PatientResponse toResponse(Patient patient, long emergencyContactCount, long insuranceCount) {
		if (patient == null) {
			return null;
		}
		PatientResponse response = new PatientResponse();
		response.setId(patient.getId());
		response.setFirstName(patient.getFirstName());
		response.setLastName(patient.getLastName());
		response.setNationalId(patient.getNationalId());
		response.setBirthDate(patient.getBirthDate());
		response.setGender(patient.getGender());
		response.setContact(commonValueObjectMapper.toDto(patient.getContact()));
		response.setAddress(commonValueObjectMapper.toDto(patient.getAddress()));
		response.setEmergencyContactCount(emergencyContactCount);
		response.setInsuranceCount(insuranceCount);
		response.setCreatedAt(patient.getCreatedAt());
		response.setUpdatedAt(patient.getUpdatedAt());
		return response;
	}
}
