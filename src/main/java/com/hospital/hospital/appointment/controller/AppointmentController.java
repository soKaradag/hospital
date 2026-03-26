package com.hospital.hospital.appointment.controller;

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

import com.hospital.hospital.appointment.dto.AppointmentResponse;
import com.hospital.hospital.appointment.dto.CreateAppointmentRequest;
import com.hospital.hospital.appointment.dto.UpdateAppointmentRequest;
import com.hospital.hospital.appointment.service.AppointmentService;
import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.common.dto.PageResponse;

import jakarta.validation.Valid;

/*
- Bu controller, randevu domain'ine ait HTTP isteklerini karşılayan giriş katmanıdır.
- Görevi iş kuralı yazmak değil, gelen request'i doğrulayıp uygun service metoduna yönlendirmektir.
- @RestController sayesinde dönen veriler JSON response olarak istemciye iletilir.
- @RequestMapping("/api/appointments") bu controller içindeki tüm endpoint'lerin ortak kök yolunu belirler.
- Controller içinde doğrudan repository kullanılmaz; veri erişim ve iş kuralı tamamen service katmanında tutulur.
- Başarılı sonuçlar ortak API standardını korumak için ApiResponse ile sarılarak döndürülür.
- Liste endpoint'lerinde doğrudan Page nesnesi dönmek yerine PageResponse kullanılır; böylece dış API daha kontrollü hale gelir.
- search, patientId, doctorId ve tarih aralığı filtreleri tek endpoint altında toplanmıştır.
- Filtre sırası bilinçlidir; arama varsa search çalışır, aksi halde domain filtreleri değerlendirilir.
- Pageable parametresi sayesinde tüm listeleme işlemleri sayfalı yürür ve büyük veri setlerinde kontrol sağlanır.
*/
@Validated
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

	private final AppointmentService appointmentService;

	public AppointmentController(AppointmentService appointmentService) {
		this.appointmentService = appointmentService;
	}

	//PostMapping Anatasyonu; HTTP POST isteği ile randevu oluşturmak için kullanılır.
	//Post isteği yeni bir kaynak oluşturmak için kullanılır.
	@PostMapping
	public ApiResponse<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest request) {
		return ApiResponse.success("Appointment created successfully", appointmentService.create(request));
	}

	//PutMapping Anatasyonu; HTTP PUT isteği ile randevu güncellemek için kullanılır.
	//Put isteği mevcut bir kaynağı güncellemek için kullanılır.
	@PutMapping("/{id}")
	public ApiResponse<AppointmentResponse> update(@PathVariable UUID id,
			@Valid @RequestBody UpdateAppointmentRequest request) {
		return ApiResponse.success("Appointment updated successfully", appointmentService.update(id, request));
	}

	//GetMapping Anatasyonu; HTTP GET isteği ile randevu getirmek için kullanılır.
	//Get isteği mevcut bir kaynağı getirmek için kullanılır.
	@GetMapping("/{id}")
	public ApiResponse<AppointmentResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Appointment retrieved successfully", appointmentService.getById(id));
	}

	@GetMapping
	public ApiResponse<PageResponse<AppointmentResponse>> getAll(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) UUID patientId,
			@RequestParam(required = false) UUID doctorId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDateTime,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDateTime,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success("Appointments searched successfully",
					PageResponse.from(appointmentService.search(search, pageable)));
		}
		if (patientId != null) {
			return ApiResponse.success("Appointments retrieved successfully",
					PageResponse.from(appointmentService.getAllByPatient(patientId, pageable)));
		}
		if (doctorId != null) {
			return ApiResponse.success("Appointments retrieved successfully",
					PageResponse.from(appointmentService.getAllByDoctor(doctorId, pageable)));
		}
		if (startDateTime != null && endDateTime != null) {
			return ApiResponse.success("Appointments retrieved successfully",
					PageResponse.from(appointmentService.getAllByDateRange(startDateTime, endDateTime, pageable)));
		}
		// ApiResponse kullanımı; PageResponse.from() metodu ile Page nesnesini PageResponse nesnesine dönüştürür.
		return ApiResponse.success("Appointments retrieved successfully",
				PageResponse.from(appointmentService.getAll(pageable)));
	}
}
