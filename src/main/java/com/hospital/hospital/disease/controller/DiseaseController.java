package com.hospital.hospital.disease.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital.auth.annotation.RequirePermission;
import com.hospital.hospital.auth.model.PermissionCodes;
import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.common.dto.PageResponse;
import com.hospital.hospital.disease.dto.CreateDiseaseRequest;
import com.hospital.hospital.disease.dto.DiseaseResponse;
import com.hospital.hospital.disease.dto.UpdateDiseaseRequest;
import com.hospital.hospital.disease.service.DiseaseService;

import jakarta.validation.Valid;

/*
- Bu controller hastalık kataloguna ait HTTP giriş noktalarını toplar.
- Amaç, tekrar kullanılabilir hastalık sözlüğünü yönetmek ve diğer domainler için referans veri sağlamaktır.
- Yazma işlemleri daha dar rol seti ile korunur; okuma işlemleri klinik ve operasyon ekiplerine açılır.
- Controller içinde iş kuralı tutulmaz; duplicate code kontrolü ve benzeri kurallar service katmanında çözülür.
*/
@Validated
@RestController
@RequestMapping("/api/diseases")
@RequirePermission(PermissionCodes.DISEASES_READ)
public class DiseaseController {

	private final DiseaseService diseaseService;

	public DiseaseController(DiseaseService diseaseService) {
		this.diseaseService = diseaseService;
	}

	// Yeni hastalık katalog kaydı oluşturur.
	@PostMapping
	@RequirePermission(PermissionCodes.DISEASES_WRITE)
	public ApiResponse<DiseaseResponse> create(@Valid @RequestBody CreateDiseaseRequest request) {
		return ApiResponse.success("Disease created successfully", diseaseService.create(request));
	}

	// Mevcut hastalık katalog kaydını günceller.
	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.DISEASES_WRITE)
	public ApiResponse<DiseaseResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateDiseaseRequest request) {
		return ApiResponse.success("Disease updated successfully", diseaseService.update(id, request));
	}

	// Tekil hastalık kaydını kimliğine göre getirir.
	@GetMapping("/{id}")
	public ApiResponse<DiseaseResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Disease retrieved successfully", diseaseService.getById(id));
	}

	// Bu endpoint hem sayfalı listeleme hem de kod/ad bazlı aramayı tek noktadan yönetir.
	@GetMapping
	public ApiResponse<PageResponse<DiseaseResponse>> getAll(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success("Diseases searched successfully",
					PageResponse.from(diseaseService.search(search, pageable)));
		}
		return ApiResponse.success("Diseases retrieved successfully", PageResponse.from(diseaseService.getAll(pageable)));
	}
}
