package com.hospital.hospital.department.controller;

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
import com.hospital.hospital.department.dto.CreateDepartmentRequest;
import com.hospital.hospital.department.dto.DepartmentResponse;
import com.hospital.hospital.department.dto.UpdateDepartmentRequest;
import com.hospital.hospital.department.service.DepartmentService;

import jakarta.validation.Valid;

/*
- Bu controller, department domain'ine ait HTTP isteklerini karşılayan giriş katmanıdır.
- Görevi iş kuralı yazmak değil, gelen request'i doğrulayıp uygun service metoduna yönlendirmektir.
- @RestController sayesinde dönen veriler JSON response olarak istemciye iletilir.
- @RequestMapping("/api/departments") bu controller içindeki tüm endpoint'lerin ortak kök yolunu belirler.
- Controller içinde doğrudan repository kullanılmaz; veri erişim ve iş kuralı tamamen service katmanında tutulur.
- Başarılı sonuçlar ortak API standardını korumak için ApiResponse ile sarılarak döndürülür.
- Liste endpoint'lerinde doğrudan Page nesnesi dönmek yerine PageResponse kullanılır; böylece dış API daha kontrollü hale gelir.
- search parametresi ile bölüm adı üzerinden arama yapılabilir.
- Pageable parametresi sayesinde tüm listeleme işlemleri sayfalı yürür ve büyük veri setlerinde kontrol sağlanır.
*/
@Validated
@RestController
@RequestMapping("/api/departments")
@RequirePermission(PermissionCodes.DEPARTMENTS_READ)
public class DepartmentController {

	private final DepartmentService departmentService;

	public DepartmentController(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	// PostMapping anotasyonu; HTTP POST isteği ile yeni bölüm oluşturmak için kullanılır.
	// @Valid ve @RequestBody birlikte kullanılarak request body'si doğrulanır ve nesneye çevrilir.
	@PostMapping
	@RequirePermission(PermissionCodes.DEPARTMENTS_WRITE)
	public ApiResponse<DepartmentResponse> create(@Valid @RequestBody CreateDepartmentRequest request) {
		return ApiResponse.success("Department created successfully", departmentService.create(request));
	}

	// PutMapping anotasyonu; HTTP PUT isteği ile mevcut bölüm kaydını güncellemek için kullanılır.
	// PathVariable içindeki id, güncellenecek kaynağın kimliğini temsil eder.
	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.DEPARTMENTS_WRITE)
	public ApiResponse<DepartmentResponse> update(@PathVariable UUID id,
			@Valid @RequestBody UpdateDepartmentRequest request) {
		return ApiResponse.success("Department updated successfully", departmentService.update(id, request));
	}

	// GetMapping anotasyonu; HTTP GET isteği ile tek bir bölüm kaydını getirmek için kullanılır.
	@GetMapping("/{id}")
	public ApiResponse<DepartmentResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Department retrieved successfully", departmentService.getById(id));
	}

	// Bu endpoint hem sayfalı listeleme hem de arama işlemini tek noktadan yönetir.
	// search doluysa service.search(...) çağrılır, aksi halde service.getAll(...) çalışır.
	// ApiResponse kullanımı; PageResponse.from() metodu ile Page nesnesi dış API için sadeleştirilir.
	@GetMapping
	public ApiResponse<PageResponse<DepartmentResponse>> getAll(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success("Departments searched successfully",
					PageResponse.from(departmentService.search(search, pageable)));
		}
		return ApiResponse.success("Departments retrieved successfully",
				PageResponse.from(departmentService.getAll(pageable)));
	}
}
