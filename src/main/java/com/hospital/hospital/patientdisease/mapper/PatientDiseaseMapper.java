package com.hospital.hospital.patientdisease.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.patientdisease.dto.CreatePatientDiseaseRequest;
import com.hospital.hospital.patientdisease.dto.PatientDiseaseResponse;
import com.hospital.hospital.patientdisease.dto.UpdatePatientDiseaseRequest;
import com.hospital.hospital.patientdisease.model.PatientDisease;

// PatientDisease entity ile request/response DTO'ları arasındaki manuel dönüşümleri yönetir.
@Component
public class PatientDiseaseMapper {

	// Create request'ten ilişki entity'sinin doğrudan yazılabilir alanlarını taşır.
	public PatientDisease toEntity(CreatePatientDiseaseRequest request) {
		if (request == null) {
			return null;
		}
		PatientDisease patientDisease = new PatientDisease();
		patientDisease.setDiagnosedAt(request.getDiagnosedAt());
		patientDisease.setNotes(request.getNotes());
		return patientDisease;
	}

	// Update request içeriğini mevcut ilişki entity'sine uygular.
	public void updateEntity(UpdatePatientDiseaseRequest request, PatientDisease patientDisease) {
		if (request == null || patientDisease == null) {
			return;
		}
		patientDisease.setDiagnosedAt(request.getDiagnosedAt());
		patientDisease.setNotes(request.getNotes());
	}

	// Entity alanlarını dış API için açıklayıcı response modeline dönüştürür.
	// Hem patient hem disease tarafından özet alanlar eklenerek istemcinin ek sorgu ihtiyacı azaltılır.
	public PatientDiseaseResponse toResponse(PatientDisease patientDisease) {
		return toResponse(patientDisease, 0L, 0L);
	}

	public PatientDiseaseResponse toResponse(PatientDisease patientDisease, long statusHistoryCount, long followupCount) {
		if (patientDisease == null) {
			return null;
		}
		PatientDiseaseResponse response = new PatientDiseaseResponse();
		response.setId(patientDisease.getId());
		if (patientDisease.getPatient() != null) {
			response.setPatientId(patientDisease.getPatient().getId());
			response.setPatientFullName(
					patientDisease.getPatient().getFirstName() + " " + patientDisease.getPatient().getLastName());
		}
		if (patientDisease.getDisease() != null) {
			response.setDiseaseId(patientDisease.getDisease().getId());
			response.setDiseaseCode(patientDisease.getDisease().getCode());
			response.setDiseaseName(patientDisease.getDisease().getName());
		}
		response.setDiagnosedAt(patientDisease.getDiagnosedAt());
		response.setNotes(patientDisease.getNotes());
		response.setStatusHistoryCount(statusHistoryCount);
		response.setFollowupCount(followupCount);
		response.setCreatedAt(patientDisease.getCreatedAt());
		response.setUpdatedAt(patientDisease.getUpdatedAt());
		return response;
	}
}
