package com.hospital.hospital.patient.controller;

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

import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.common.dto.PageResponse;
import com.hospital.hospital.patient.dto.CreatePatientRequest;
import com.hospital.hospital.patient.dto.PatientResponse;
import com.hospital.hospital.patient.dto.UpdatePatientRequest;
import com.hospital.hospital.patient.service.PatientService;

import jakarta.validation.Valid;

/*
- Bu controller, patient domain'ine ait HTTP isteklerini karşılayan giriş katmanıdır.
- Görevi iş kuralı yazmak değil, gelen request'i doğrulayıp uygun service metoduna yönlendirmektir.
- @RestController sayesinde dönen veriler JSON response olarak istemciye iletilir.
- @RequestMapping("/api/patients") bu controller içindeki tüm endpoint'lerin ortak kök yolunu belirler.
- Controller içinde doğrudan repository kullanılmaz; veri erişim ve iş kuralı tamamen service katmanında tutulur.
- Başarılı sonuçlar ortak API standardını korumak için ApiResponse ile sarılarak döndürülür.
- Liste endpoint'lerinde doğrudan Page nesnesi dönmek yerine PageResponse kullanılır; böylece dış API daha kontrollü hale gelir.
- search parametresi hasta adı, soyadı ve nationalId alanlarında arama yapmak için kullanılır.
- Pageable parametresi sayesinde tüm listeleme işlemleri sayfalı yürür ve büyük veri setlerinde kontrol sağlanır.
*/
@Validated
@RestController
@RequestMapping("/api/patients")
public class PatientController {

	private final PatientService patientService;

	public PatientController(PatientService patientService) {
		this.patientService = patientService;
	}

	// PostMapping anotasyonu; HTTP POST isteği ile yeni hasta kaydı oluşturmak için kullanılır.
	@PostMapping
	public ApiResponse<PatientResponse> create(@Valid @RequestBody CreatePatientRequest request) {
		return ApiResponse.success("Patient created successfully", patientService.create(request));
	}

	// PutMapping anotasyonu; HTTP PUT isteği ile mevcut hasta kaydını güncellemek için kullanılır.
	@PutMapping("/{id}")
	public ApiResponse<PatientResponse> update(@PathVariable UUID id,
			@Valid @RequestBody UpdatePatientRequest request) {
		return ApiResponse.success("Patient updated successfully", patientService.update(id, request));
	}

	// GetMapping anotasyonu; HTTP GET isteği ile tek bir hasta kaydını getirmek için kullanılır.
	@GetMapping("/{id}")
	public ApiResponse<PatientResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Patient retrieved successfully", patientService.getById(id));
	}

	// Bu endpoint hem sayfalı listeleme hem de arama işlemini tek noktadan yönetir.
	// search doluysa service.search(...) çağrılır, aksi halde service.getAll(...) çalışır.
	// ApiResponse kullanımı; PageResponse.from() metodu ile Page nesnesi dış API için sadeleştirilir.
	@GetMapping
	public ApiResponse<PageResponse<PatientResponse>> getAll(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success("Patients searched successfully",
					PageResponse.from(patientService.search(search, pageable)));
		}
		return ApiResponse.success("Patients retrieved successfully",
				PageResponse.from(patientService.getAll(pageable)));
	}
}
