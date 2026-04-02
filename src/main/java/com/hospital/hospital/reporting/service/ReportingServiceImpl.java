package com.hospital.hospital.reporting.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.reporting.dto.DoctorWorkloadReportResponse;
import com.hospital.hospital.reporting.dto.PatientPaymentSummaryResponse;
import com.hospital.hospital.reporting.dto.PaymentAuditResponse;
import com.hospital.hospital.reporting.repository.DoctorWorkloadViewRepository;
import com.hospital.hospital.reporting.repository.PatientPaymentSummaryProjection;
import com.hospital.hospital.reporting.repository.PatientPaymentSummaryRepository;
import com.hospital.hospital.reporting.repository.PaymentAuditRepository;

/*
- Reporting service, ileri veritabanı örneklerini controller katmanına sade biçimde sunar.
- Burada view, native query ve trigger sonuçları uygulama seviyesinde anlamlı response nesnelerine çevrilir.
- SQL scriptleri çalıştırılmadığında teknik hata yerine daha anlaşılır iş kuralı mesajı üretmek de bu katmanda yapılır.
*/
@Service
public class ReportingServiceImpl implements ReportingService {

	private final DoctorWorkloadViewRepository doctorWorkloadViewRepository;
	private final PatientPaymentSummaryRepository patientPaymentSummaryRepository;
	private final PaymentAuditRepository paymentAuditRepository;

	public ReportingServiceImpl(
			DoctorWorkloadViewRepository doctorWorkloadViewRepository,
			PatientPaymentSummaryRepository patientPaymentSummaryRepository,
			PaymentAuditRepository paymentAuditRepository) {
		this.doctorWorkloadViewRepository = doctorWorkloadViewRepository;
		this.patientPaymentSummaryRepository = patientPaymentSummaryRepository;
		this.paymentAuditRepository = paymentAuditRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DoctorWorkloadReportResponse> getDoctorWorkloadReport() {
		try {
			return doctorWorkloadViewRepository.findAll();
		} catch (DataAccessException exception) {
			throw new BusinessRuleViolationException(
					"Doctor workload view is not available. Run phase-3 advanced SQL scripts first.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PatientPaymentSummaryResponse> getPatientPaymentSummary() {
		return patientPaymentSummaryRepository.findPatientPaymentSummaries()
				.stream()
				.map(this::toPatientPaymentSummaryResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<PaymentAuditResponse> getRecentPaymentAudits(int limit) {
		try {
			return paymentAuditRepository.findRecent(limit);
		} catch (DataAccessException exception) {
			throw new BusinessRuleViolationException(
					"Payment audit objects are not available. Run phase-3 advanced SQL scripts first.");
		}
	}

	private PatientPaymentSummaryResponse toPatientPaymentSummaryResponse(PatientPaymentSummaryProjection projection) {
		return new PatientPaymentSummaryResponse(
				UUID.fromString(projection.getPatientId()),
				projection.getPatientFullName(),
				projection.getPaymentCount() != null ? projection.getPaymentCount() : 0L,
				projection.getTotalPaidAmount(),
				projection.getLastPaidAt() != null ? projection.getLastPaidAt().toInstant() : null);
	}
}
