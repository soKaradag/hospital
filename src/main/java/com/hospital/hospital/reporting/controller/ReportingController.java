package com.hospital.hospital.reporting.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital.auth.annotation.RequireRole;
import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.reporting.dto.DoctorWorkloadReportResponse;
import com.hospital.hospital.reporting.dto.PatientPaymentSummaryResponse;
import com.hospital.hospital.reporting.dto.PaymentAuditResponse;
import com.hospital.hospital.reporting.service.ReportingService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/*
- Bu controller, ileri veritabanı örneklerini API seviyesinde görünür kılar.
- Amaç sadece CRUD ekranları değil, view, trigger ve native SQL gibi veri erişim senaryolarını da sunabilmektir.
- Controller iş kuralı yazmaz; yalnızca uygun reporting service metoduna yönlendirir.
*/
@Validated
@RestController
@RequestMapping("/api/reports")
@RequireRole({ Role.ADMIN, Role.DOCTOR, Role.RECEPTIONIST, Role.CASHIER, Role.NURSE })
public class ReportingController {

	private final ReportingService reportingService;

	public ReportingController(ReportingService reportingService) {
		this.reportingService = reportingService;
	}

	// View tabanlı rapor, doktor iş yükünü tek endpoint üzerinden döndürür.
	@GetMapping("/doctor-workload")
	public ApiResponse<List<DoctorWorkloadReportResponse>> getDoctorWorkloadReport() {
		return ApiResponse.success("Doctor workload report retrieved successfully",
				reportingService.getDoctorWorkloadReport());
	}

	// Native SQL aggregation sonucu, hasta bazlı ödeme özetini döndürür.
	@GetMapping("/patient-payment-summary")
	public ApiResponse<List<PatientPaymentSummaryResponse>> getPatientPaymentSummary() {
		return ApiResponse.success("Patient payment summary retrieved successfully",
				reportingService.getPatientPaymentSummary());
	}

	// Trigger tarafından doldurulan audit tablosunun son kayıtları gözlem amaçlı döndürülür.
	@GetMapping("/payment-audits")
	public ApiResponse<List<PaymentAuditResponse>> getRecentPaymentAudits(
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit) {
		return ApiResponse.success("Payment audit records retrieved successfully",
				reportingService.getRecentPaymentAudits(limit));
	}
}
