package com.hospital.hospital.prescription.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.hospital.hospital.prescription.dto.CreatePrescriptionRequest;
import com.hospital.hospital.prescription.dto.PrescriptionResponse;
import com.hospital.hospital.prescription.dto.UpdatePrescriptionRequest;
import com.hospital.hospital.prescription.service.PrescriptionService;

import jakarta.validation.Valid;

/*
- Bu controller reçete üst kayıtlarının HTTP giriş noktalarını yönetir.
- Reçete, encounter sonrası üretilen klinik çıktıdır ve patient/doctor/encounter ilişkilerini birlikte taşır.
- Yazma işlemleri daha dar klinik rol seti ile korunur.
*/
@Validated
@RestController
@RequestMapping("/api/prescriptions")
@RequirePermission(PermissionCodes.PRESCRIPTIONS_READ)
public class PrescriptionController {

	private final PrescriptionService prescriptionService;

	public PrescriptionController(PrescriptionService prescriptionService) {
		this.prescriptionService = prescriptionService;
	}

	// Yeni reçete üst kaydı oluşturur.
	@PostMapping
	@RequirePermission(PermissionCodes.PRESCRIPTIONS_WRITE)
	public ApiResponse<PrescriptionResponse> create(@Valid @RequestBody CreatePrescriptionRequest request) {
		return ApiResponse.success("Prescription created successfully", prescriptionService.create(request));
	}

	// Mevcut reçete kaydını günceller.
	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.PRESCRIPTIONS_WRITE)
	public ApiResponse<PrescriptionResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdatePrescriptionRequest request) {
		return ApiResponse.success("Prescription updated successfully", prescriptionService.update(id, request));
	}

	// Tekil reçete kaydını getirir.
	@GetMapping("/{id}")
	public ApiResponse<PrescriptionResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Prescription retrieved successfully", prescriptionService.getById(id));
	}

	// Bu endpoint tüm reçeteleri veya encounter/patient/doctor/tarih filtresiyle alt kümeleri sayfalı şekilde döner.
	@GetMapping
	public ApiResponse<PageResponse<PrescriptionResponse>> getAll(
			@RequestParam(required = false) UUID encounterId,
			@RequestParam(required = false) UUID patientId,
			@RequestParam(required = false) UUID doctorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@PageableDefault(size = 20) Pageable pageable) {
		if (encounterId != null) {
			return ApiResponse.success("Prescriptions retrieved successfully",
					PageResponse.from(prescriptionService.getAllByEncounter(encounterId, pageable)));
		}
		if (patientId != null) {
			return ApiResponse.success("Prescriptions retrieved successfully",
					PageResponse.from(prescriptionService.getAllByPatient(patientId, pageable)));
		}
		if (doctorId != null) {
			return ApiResponse.success("Prescriptions retrieved successfully",
					PageResponse.from(prescriptionService.getAllByDoctor(doctorId, pageable)));
		}
		if (startDate != null && endDate != null) {
			return ApiResponse.success("Prescriptions retrieved successfully",
					PageResponse.from(prescriptionService.getAllByDateRange(startDate, endDate, pageable)));
		}
		return ApiResponse.success("Prescriptions retrieved successfully",
				PageResponse.from(prescriptionService.getAll(pageable)));
	}
}
