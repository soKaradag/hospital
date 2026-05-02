package com.hospital.hospital.disease.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.disease.dto.CreateDiseaseRequest;
import com.hospital.hospital.disease.dto.DiseaseResponse;
import com.hospital.hospital.disease.dto.UpdateDiseaseRequest;
import com.hospital.hospital.disease.model.Disease;

// Disease entity ile request/response DTO'ları arasındaki manuel dönüşümleri yönetir.
@Component
public class DiseaseMapper {

	// Create request içeriğini yeni entity örneğine taşır.
	public Disease toEntity(CreateDiseaseRequest request) {
		if (request == null) {
			return null;
		}
		Disease disease = new Disease();
		disease.setCode(request.getCode());
		disease.setName(request.getName());
		disease.setDescription(request.getDescription());
		return disease;
	}

	// Güncelleme isteğindeki alanları mevcut entity üzerine yazar.
	public void updateEntity(UpdateDiseaseRequest request, Disease disease) {
		if (request == null || disease == null) {
			return;
		}
		disease.setCode(request.getCode());
		disease.setName(request.getName());
		disease.setDescription(request.getDescription());
	}

	// Entity alanlarını dış API için sade response modeline dönüştürür.
	public DiseaseResponse toResponse(Disease disease) {
		return toResponse(disease, 0L);
	}

	public DiseaseResponse toResponse(Disease disease, long codeMappingCount) {
		if (disease == null) {
			return null;
		}
		DiseaseResponse response = new DiseaseResponse();
		response.setId(disease.getId());
		response.setCode(disease.getCode());
		response.setName(disease.getName());
		response.setDescription(disease.getDescription());
		if (disease.getCategory() != null) {
			response.setCategoryCode(disease.getCategory().getCode());
			response.setCategoryName(disease.getCategory().getName());
		}
		response.setCodeMappingCount(codeMappingCount);
		response.setCreatedAt(disease.getCreatedAt());
		response.setUpdatedAt(disease.getUpdatedAt());
		return response;
	}
}
