package com.hospital.hospital.reporting.service;

import java.util.List;

import com.hospital.hospital.reporting.dto.DoctorWorkloadReportResponse;
import com.hospital.hospital.reporting.dto.PatientPaymentSummaryResponse;
import com.hospital.hospital.reporting.dto.PaymentAuditResponse;

// Reporting tarafında sunulacak ileri veritabanı örneklerini tek arayüzde toplar.
public interface ReportingService {

	List<DoctorWorkloadReportResponse> getDoctorWorkloadReport();

	List<PatientPaymentSummaryResponse> getPatientPaymentSummary();

	List<PaymentAuditResponse> getRecentPaymentAudits(int limit);
}
