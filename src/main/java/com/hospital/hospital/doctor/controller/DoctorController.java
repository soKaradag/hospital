package com.hospital.hospital.doctor.controller;

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
import com.hospital.hospital.doctor.dto.CreateDoctorRequest;
import com.hospital.hospital.doctor.dto.DoctorResponse;
import com.hospital.hospital.doctor.dto.UpdateDoctorRequest;
import com.hospital.hospital.doctor.service.DoctorService;

import jakarta.validation.Valid;

/*
- Bu controller, doctor domain'ine ait HTTP isteklerini karşılayan giriş katmanıdır.
- Görevi iş kuralı yazmak değil, gelen request'i doğrulayıp uygun service metoduna yönlendirmektir.
- @RestController sayesinde dönen veriler JSON response olarak istemciye iletilir.
- @RequestMapping("/api/doctors") bu controller içindeki tüm endpoint'lerin ortak kök yolunu belirler.
- Controller içinde doğrudan repository kullanılmaz; veri erişim ve iş kuralı tamamen service katmanında tutulur.
- Başarılı sonuçlar ortak API standardını korumak için ApiResponse ile sarılarak döndürülür.
- Liste endpoint'lerinde doğrudan Page nesnesi dönmek yerine PageResponse kullanılır; böylece dış API daha kontrollü hale gelir.
- search parametresi doktor adı, soyadı ve uzmanlık alanında arama yapmak için kullanılır.
- departmentId parametresi ile belirli bir bölüme bağlı doktorlar filtrelenebilir.
- Pageable parametresi sayesinde tüm listeleme işlemleri sayfalı yürür ve büyük veri setlerinde kontrol sağlanır.
*/
@Validated
@RestController
@RequestMapping("/api/doctors")
@RequirePermission(PermissionCodes.DOCTORS_READ)
public class DoctorController {

	private final DoctorService doctorService;

	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	// PostMapping anotasyonu; HTTP POST isteği ile yeni doktor kaydı oluşturmak için kullanılır.
	@PostMapping
	@RequirePermission(PermissionCodes.DOCTORS_WRITE)
	public ApiResponse<DoctorResponse> create(@Valid @RequestBody CreateDoctorRequest request) {
		return ApiResponse.success("Doctor created successfully", doctorService.create(request));
	}

	// PutMapping anotasyonu; HTTP PUT isteği ile mevcut doktor kaydını güncellemek için kullanılır.
	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.DOCTORS_WRITE)
	public ApiResponse<DoctorResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateDoctorRequest request) {
		return ApiResponse.success("Doctor updated successfully", doctorService.update(id, request));
	}

	// GetMapping anotasyonu; HTTP GET isteği ile tek bir doktor kaydını getirmek için kullanılır.
	@GetMapping("/{id}")
	public ApiResponse<DoctorResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Doctor retrieved successfully", doctorService.getById(id));
	}

	// Bu endpoint hem sayfalı listeleme hem de filtreleme işlemini tek noktadan yönetir.
	// search varsa arama yapılır, departmentId varsa bölüm bazlı filtre çalışır, yoksa tüm kayıtlar listelenir.
	// ApiResponse kullanımı; PageResponse.from() metodu ile Page nesnesi dış API için sadeleştirilir.
	@GetMapping
	public ApiResponse<PageResponse<DoctorResponse>> getAll(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) UUID departmentId,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success("Doctors searched successfully",
					PageResponse.from(doctorService.search(search, pageable)));
		}
		if (departmentId != null) {
			return ApiResponse.success("Doctors retrieved successfully",
					PageResponse.from(doctorService.getAllByDepartment(departmentId, pageable)));
		}
		return ApiResponse.success("Doctors retrieved successfully",
				PageResponse.from(doctorService.getAll(pageable)));
	}
}
