package com.hospital.hospital.patientdisease.controller;

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
import com.hospital.hospital.patientdisease.dto.CreatePatientDiseaseRequest;
import com.hospital.hospital.patientdisease.dto.PatientDiseaseResponse;
import com.hospital.hospital.patientdisease.dto.UpdatePatientDiseaseRequest;
import com.hospital.hospital.patientdisease.service.PatientDiseaseService;

import jakarta.validation.Valid;

/*
- Bu controller hastanın bilinen hastalık geçmişi endpoint'lerini yönetir.
- Disease kataloğu ayrı tutulurken, hasta ile hastalık arasındaki ilişki burada yönetilir.
- Yazma işlemleri klinik rollere açılır; okuma işlemleri operasyon tarafına da kontrollü şekilde sunulur.
*/
@Validated
@RestController
@RequestMapping("/api/patient-diseases")
@RequirePermission(PermissionCodes.PATIENT_DISEASES_READ)
public class PatientDiseaseController {

	private final PatientDiseaseService patientDiseaseService;

	public PatientDiseaseController(PatientDiseaseService patientDiseaseService) {
		this.patientDiseaseService = patientDiseaseService;
	}

	// Hasta ile hastalık arasında yeni bir geçmiş kaydı oluşturur.
	@PostMapping
	@RequirePermission(PermissionCodes.PATIENT_DISEASES_WRITE)
	public ApiResponse<PatientDiseaseResponse> create(@Valid @RequestBody CreatePatientDiseaseRequest request) {
		return ApiResponse.success("Patient disease history created successfully", patientDiseaseService.create(request));
	}

	// Mevcut hasta-hastalık geçmiş kaydını günceller.
	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.PATIENT_DISEASES_WRITE)
	public ApiResponse<PatientDiseaseResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdatePatientDiseaseRequest request) {
		return ApiResponse.success("Patient disease history updated successfully", patientDiseaseService.update(id, request));
	}

	// Tekil hasta-hastalık geçmiş kaydını getirir.
	@GetMapping("/{id}")
	public ApiResponse<PatientDiseaseResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Patient disease history retrieved successfully", patientDiseaseService.getById(id));
	}

	// Bu endpoint tüm kayıtları veya patient/disease filtresiyle alt kümeleri sayfalı şekilde döner.
	@GetMapping
	public ApiResponse<PageResponse<PatientDiseaseResponse>> getAll(
			@RequestParam(required = false) UUID patientId,
			@RequestParam(required = false) UUID diseaseId,
			@PageableDefault(size = 20) Pageable pageable) {
		if (patientId != null) {
			return ApiResponse.success("Patient disease histories retrieved successfully",
					PageResponse.from(patientDiseaseService.getAllByPatient(patientId, pageable)));
		}
		if (diseaseId != null) {
			return ApiResponse.success("Patient disease histories retrieved successfully",
					PageResponse.from(patientDiseaseService.getAllByDisease(diseaseId, pageable)));
		}
		return ApiResponse.success("Patient disease histories retrieved successfully",
				PageResponse.from(patientDiseaseService.getAll(pageable)));
	}
}
