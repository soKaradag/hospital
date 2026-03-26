package com.hospital.hospital.encounter.controller;

import java.time.Instant;
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

import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.common.dto.PageResponse;
import com.hospital.hospital.encounter.dto.CreateEncounterRequest;
import com.hospital.hospital.encounter.dto.EncounterResponse;
import com.hospital.hospital.encounter.dto.UpdateEncounterRequest;
import com.hospital.hospital.encounter.service.EncounterService;

import jakarta.validation.Valid;

/*
- Bu controller, encounter domain'ine ait HTTP isteklerini karşılayan giriş katmanıdır.
- Görevi iş kuralı yazmak değil, gelen request'i doğrulayıp uygun service metoduna yönlendirmektir.
- @RestController sayesinde dönen veriler JSON response olarak istemciye iletilir.
- @RequestMapping("/api/encounters") bu controller içindeki tüm endpoint'lerin ortak kök yolunu belirler.
- Controller içinde doğrudan repository kullanılmaz; veri erişim ve iş kuralı tamamen service katmanında tutulur.
- Başarılı sonuçlar ortak API standardını korumak için ApiResponse ile sarılarak döndürülür.
- Liste endpoint'lerinde doğrudan Page nesnesi dönmek yerine PageResponse kullanılır; böylece dış API daha kontrollü hale gelir.
- search, patientId, doctorId ve tarih aralığı filtreleri tek endpoint altında toplanmıştır.
- Filtre sırası bilinçlidir; arama varsa search çalışır, aksi halde domain filtreleri değerlendirilir.
- Pageable parametresi sayesinde tüm listeleme işlemleri sayfalı yürür ve büyük veri setlerinde kontrol sağlanır.
*/
@Validated
@RestController
@RequestMapping("/api/encounters")
public class EncounterController {

	private final EncounterService encounterService;

	public EncounterController(EncounterService encounterService) {
		this.encounterService = encounterService;
	}

	// PostMapping anotasyonu; HTTP POST isteği ile yeni muayene kaydı oluşturmak için kullanılır.
	@PostMapping
	public ApiResponse<EncounterResponse> create(@Valid @RequestBody CreateEncounterRequest request) {
		return ApiResponse.success("Encounter created successfully", encounterService.create(request));
	}

	// PutMapping anotasyonu; HTTP PUT isteği ile mevcut muayene kaydını güncellemek için kullanılır.
	@PutMapping("/{id}")
	public ApiResponse<EncounterResponse> update(@PathVariable UUID id,
			@Valid @RequestBody UpdateEncounterRequest request) {
		return ApiResponse.success("Encounter updated successfully", encounterService.update(id, request));
	}

	// GetMapping anotasyonu; HTTP GET isteği ile tek bir muayene kaydını getirmek için kullanılır.
	@GetMapping("/{id}")
	public ApiResponse<EncounterResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Encounter retrieved successfully", encounterService.getById(id));
	}

	// Bu endpoint hem sayfalı listeleme hem de filtreleme işlemini tek noktadan yönetir.
	// search varsa arama yapılır; patientId, doctorId ve tarih aralığına göre filtreleme de desteklenir.
	// ApiResponse kullanımı; PageResponse.from() metodu ile Page nesnesi dış API için sadeleştirilir.
	@GetMapping
	public ApiResponse<PageResponse<EncounterResponse>> getAll(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) UUID patientId,
			@RequestParam(required = false) UUID doctorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDateTime,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDateTime,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success("Encounters searched successfully",
					PageResponse.from(encounterService.search(search, pageable)));
		}
		if (patientId != null) {
			return ApiResponse.success("Encounters retrieved successfully",
					PageResponse.from(encounterService.getAllByPatient(patientId, pageable)));
		}
		if (doctorId != null) {
			return ApiResponse.success("Encounters retrieved successfully",
					PageResponse.from(encounterService.getAllByDoctor(doctorId, pageable)));
		}
		if (startDateTime != null && endDateTime != null) {
			return ApiResponse.success("Encounters retrieved successfully",
					PageResponse.from(encounterService.getAllByDateRange(startDateTime, endDateTime, pageable)));
		}
		return ApiResponse.success("Encounters retrieved successfully",
				PageResponse.from(encounterService.getAll(pageable)));
	}
}
