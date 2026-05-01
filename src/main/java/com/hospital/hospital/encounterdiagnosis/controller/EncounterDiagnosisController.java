package com.hospital.hospital.encounterdiagnosis.controller;

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
import com.hospital.hospital.encounterdiagnosis.dto.CreateEncounterDiagnosisRequest;
import com.hospital.hospital.encounterdiagnosis.dto.EncounterDiagnosisResponse;
import com.hospital.hospital.encounterdiagnosis.dto.UpdateEncounterDiagnosisRequest;
import com.hospital.hospital.encounterdiagnosis.service.EncounterDiagnosisService;

import jakarta.validation.Valid;

/*
- Bu controller encounter bazlı teşhis kayıtlarının HTTP giriş noktalarını toplar.
- Buradaki kayıtlar hastanın genel hastalık geçmişinden farklı olarak belirli bir muayene anında konulan teşhisleri temsil eder.
- Yazma işlemleri klinik rollere açılır; okuma işlemleri operasyon tarafına da kontrollü olarak sunulabilir.
*/
@Validated
@RestController
@RequestMapping("/api/encounter-diagnoses")
@RequirePermission(PermissionCodes.ENCOUNTER_DIAGNOSES_READ)
public class EncounterDiagnosisController {

	private final EncounterDiagnosisService encounterDiagnosisService;

	public EncounterDiagnosisController(EncounterDiagnosisService encounterDiagnosisService) {
		this.encounterDiagnosisService = encounterDiagnosisService;
	}

	// Belirli bir encounter için yeni teşhis kaydı oluşturur.
	@PostMapping
	@RequirePermission(PermissionCodes.ENCOUNTER_DIAGNOSES_WRITE)
	public ApiResponse<EncounterDiagnosisResponse> create(@Valid @RequestBody CreateEncounterDiagnosisRequest request) {
		return ApiResponse.success("Encounter diagnosis created successfully", encounterDiagnosisService.create(request));
	}

	// Mevcut encounter teşhis kaydını günceller.
	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.ENCOUNTER_DIAGNOSES_WRITE)
	public ApiResponse<EncounterDiagnosisResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateEncounterDiagnosisRequest request) {
		return ApiResponse.success("Encounter diagnosis updated successfully", encounterDiagnosisService.update(id, request));
	}

	// Tekil encounter teşhis kaydını getirir.
	@GetMapping("/{id}")
	public ApiResponse<EncounterDiagnosisResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Encounter diagnosis retrieved successfully", encounterDiagnosisService.getById(id));
	}

	// Bu endpoint tüm teşhis kayıtlarını veya encounter/disease filtresiyle alt kümeleri sayfalı şekilde döner.
	@GetMapping
	public ApiResponse<PageResponse<EncounterDiagnosisResponse>> getAll(
			@RequestParam(required = false) UUID encounterId,
			@RequestParam(required = false) UUID diseaseId,
			@PageableDefault(size = 20) Pageable pageable) {
		if (encounterId != null) {
			return ApiResponse.success("Encounter diagnoses retrieved successfully",
					PageResponse.from(encounterDiagnosisService.getAllByEncounter(encounterId, pageable)));
		}
		if (diseaseId != null) {
			return ApiResponse.success("Encounter diagnoses retrieved successfully",
					PageResponse.from(encounterDiagnosisService.getAllByDisease(diseaseId, pageable)));
		}
		return ApiResponse.success("Encounter diagnoses retrieved successfully",
				PageResponse.from(encounterDiagnosisService.getAll(pageable)));
	}
}
