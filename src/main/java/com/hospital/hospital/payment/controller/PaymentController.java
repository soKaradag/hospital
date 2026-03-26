package com.hospital.hospital.payment.controller;

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
import com.hospital.hospital.payment.dto.CreatePaymentRequest;
import com.hospital.hospital.payment.dto.PaymentResponse;
import com.hospital.hospital.payment.dto.UpdatePaymentRequest;
import com.hospital.hospital.payment.service.PaymentService;

import jakarta.validation.Valid;

/*
- Bu controller, payment domain'ine ait HTTP isteklerini karşılayan giriş katmanıdır.
- Görevi iş kuralı yazmak değil, gelen request'i doğrulayıp uygun service metoduna yönlendirmektir.
- @RestController sayesinde dönen veriler JSON response olarak istemciye iletilir.
- @RequestMapping("/api/payments") bu controller içindeki tüm endpoint'lerin ortak kök yolunu belirler.
- Controller içinde doğrudan repository kullanılmaz; veri erişim ve iş kuralı tamamen service katmanında tutulur.
- Başarılı sonuçlar ortak API standardını korumak için ApiResponse ile sarılarak döndürülür.
- Liste endpoint'lerinde doğrudan Page nesnesi dönmek yerine PageResponse kullanılır; böylece dış API daha kontrollü hale gelir.
- Payment tarafında serbest metin araması yerine patientId, encounterId ve paidAt aralığı ile filtreleme yapılır.
- Pageable parametresi sayesinde tüm listeleme işlemleri sayfalı yürür ve büyük veri setlerinde kontrol sağlanır.
*/
@Validated
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	// PostMapping anotasyonu; HTTP POST isteği ile yeni ödeme kaydı oluşturmak için kullanılır.
	@PostMapping
	public ApiResponse<PaymentResponse> create(@Valid @RequestBody CreatePaymentRequest request) {
		return ApiResponse.success("Payment created successfully", paymentService.create(request));
	}

	// PutMapping anotasyonu; HTTP PUT isteği ile mevcut ödeme kaydını güncellemek için kullanılır.
	@PutMapping("/{id}")
	public ApiResponse<PaymentResponse> update(@PathVariable UUID id,
			@Valid @RequestBody UpdatePaymentRequest request) {
		return ApiResponse.success("Payment updated successfully", paymentService.update(id, request));
	}

	// GetMapping anotasyonu; HTTP GET isteği ile tek bir ödeme kaydını getirmek için kullanılır.
	@GetMapping("/{id}")
	public ApiResponse<PaymentResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Payment retrieved successfully", paymentService.getById(id));
	}

	// Bu endpoint hem sayfalı listeleme hem de filtreleme işlemini tek noktadan yönetir.
	// patientId, encounterId ve paidAt aralığı üzerinden filtreleme yapılabilir.
	// ApiResponse kullanımı; PageResponse.from() metodu ile Page nesnesi dış API için sadeleştirilir.
	@GetMapping
	public ApiResponse<PageResponse<PaymentResponse>> getAll(
			@RequestParam(required = false) UUID patientId,
			@RequestParam(required = false) UUID encounterId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startPaidAt,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endPaidAt,
			@PageableDefault(size = 20) Pageable pageable) {
		if (patientId != null) {
			return ApiResponse.success("Payments retrieved successfully",
					PageResponse.from(paymentService.getAllByPatient(patientId, pageable)));
		}
		if (encounterId != null) {
			return ApiResponse.success("Payments retrieved successfully",
					PageResponse.from(paymentService.getAllByEncounter(encounterId, pageable)));
		}
		if (startPaidAt != null && endPaidAt != null) {
			return ApiResponse.success("Payments retrieved successfully",
					PageResponse.from(paymentService.getAllByPaidAtRange(startPaidAt, endPaidAt, pageable)));
		}
		return ApiResponse.success("Payments retrieved successfully",
				PageResponse.from(paymentService.getAll(pageable)));
	}
}
