package com.hospital.hospital.encounterdiagnosis.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.encounterdiagnosis.dto.CreateEncounterDiagnosisRequest;
import com.hospital.hospital.encounterdiagnosis.dto.EncounterDiagnosisResponse;
import com.hospital.hospital.encounterdiagnosis.dto.UpdateEncounterDiagnosisRequest;
import com.hospital.hospital.encounterdiagnosis.model.EncounterDiagnosis;

// EncounterDiagnosis entity ile request/response DTO'ları arasındaki manuel dönüşümleri yönetir.
@Component
public class EncounterDiagnosisMapper {

	// Create request içeriğinden doğrudan yazılabilir alanları yeni entity'ye taşır.
	public EncounterDiagnosis toEntity(CreateEncounterDiagnosisRequest request) {
		if (request == null) {
			return null;
		}
		EncounterDiagnosis encounterDiagnosis = new EncounterDiagnosis();
		encounterDiagnosis.setNotes(request.getNotes());
		return encounterDiagnosis;
	}

	// Update request içeriğini mevcut entity üzerine uygular.
	public void updateEntity(UpdateEncounterDiagnosisRequest request, EncounterDiagnosis encounterDiagnosis) {
		if (request == null || encounterDiagnosis == null) {
			return;
		}
		encounterDiagnosis.setNotes(request.getNotes());
	}

	// Entity alanlarını dış API için açıklayıcı response modeline dönüştürür.
	public EncounterDiagnosisResponse toResponse(EncounterDiagnosis encounterDiagnosis) {
		if (encounterDiagnosis == null) {
			return null;
		}
		EncounterDiagnosisResponse response = new EncounterDiagnosisResponse();
		response.setId(encounterDiagnosis.getId());
		if (encounterDiagnosis.getEncounter() != null) {
			response.setEncounterId(encounterDiagnosis.getEncounter().getId());
		}
		if (encounterDiagnosis.getDisease() != null) {
			response.setDiseaseId(encounterDiagnosis.getDisease().getId());
			response.setDiseaseCode(encounterDiagnosis.getDisease().getCode());
			response.setDiseaseName(encounterDiagnosis.getDisease().getName());
		}
		response.setNotes(encounterDiagnosis.getNotes());
		response.setCreatedAt(encounterDiagnosis.getCreatedAt());
		response.setUpdatedAt(encounterDiagnosis.getUpdatedAt());
		return response;
	}
}
