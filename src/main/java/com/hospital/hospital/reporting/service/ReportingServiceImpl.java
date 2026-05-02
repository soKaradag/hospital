package com.hospital.hospital.reporting.service;

import java.util.List;
import java.util.UUID;
import java.time.Instant;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.reporting.dto.DoctorWorkloadReportResponse;
import com.hospital.hospital.reporting.dto.PatientPaymentSummaryResponse;
import com.hospital.hospital.reporting.dto.PaymentAuditResponse;
import com.hospital.hospital.reporting.model.ReportExportJob;
import com.hospital.hospital.reporting.model.ReportSnapshot;
import com.hospital.hospital.reporting.repository.DoctorWorkloadViewRepository;
import com.hospital.hospital.reporting.repository.PatientPaymentSummaryProjection;
import com.hospital.hospital.reporting.repository.PatientPaymentSummaryRepository;
import com.hospital.hospital.reporting.repository.PaymentAuditRepository;
import com.hospital.hospital.reporting.repository.ReportExportJobRepository;
import com.hospital.hospital.reporting.repository.ReportSnapshotRepository;

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
	private final ReportSnapshotRepository reportSnapshotRepository;
	private final ReportExportJobRepository reportExportJobRepository;

	public ReportingServiceImpl(
			DoctorWorkloadViewRepository doctorWorkloadViewRepository,
			PatientPaymentSummaryRepository patientPaymentSummaryRepository,
			PaymentAuditRepository paymentAuditRepository,
			ReportSnapshotRepository reportSnapshotRepository,
			ReportExportJobRepository reportExportJobRepository) {
		this.doctorWorkloadViewRepository = doctorWorkloadViewRepository;
		this.patientPaymentSummaryRepository = patientPaymentSummaryRepository;
		this.paymentAuditRepository = paymentAuditRepository;
		this.reportSnapshotRepository = reportSnapshotRepository;
		this.reportExportJobRepository = reportExportJobRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DoctorWorkloadReportResponse> getDoctorWorkloadReport() {
		try {
			List<DoctorWorkloadReportResponse> report = doctorWorkloadViewRepository.findAll();
			recordReportExecution("doctor-workload", report.size(), "Doctor workload view snapshot");
			return report;
		} catch (DataAccessException exception) {
			throw new BusinessRuleViolationException(
					"Doctor workload view is not available. Run phase-3 advanced SQL scripts first.");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<PatientPaymentSummaryResponse> getPatientPaymentSummary() {
		List<PatientPaymentSummaryResponse> report = patientPaymentSummaryRepository.findPatientPaymentSummaries()
				.stream()
				.map(this::toPatientPaymentSummaryResponse)
				.toList();
		recordReportExecution("patient-payment-summary", report.size(), "Patient payment summary snapshot");
		return report;
	}

	@Override
	@Transactional(readOnly = true)
	public List<PaymentAuditResponse> getRecentPaymentAudits(int limit) {
		try {
			List<PaymentAuditResponse> report = paymentAuditRepository.findRecent(limit);
			recordReportExecution("payment-audits", report.size(), "Recent payment audit snapshot");
			return report;
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

	private void recordReportExecution(String reportCode, int rowCount, String summary) {
		ReportSnapshot snapshot = new ReportSnapshot();
		snapshot.setReportCode(reportCode);
		snapshot.setGeneratedAt(Instant.now());
		snapshot.setRowCount(rowCount);
		snapshot.setSnapshotSummary(summary);
		ReportSnapshot savedSnapshot = reportSnapshotRepository.save(snapshot);

		ReportExportJob exportJob = new ReportExportJob();
		exportJob.setReportSnapshot(savedSnapshot);
		exportJob.setExportFormat("JSON");
		exportJob.setStatus("COMPLETED");
		exportJob.setRequestedAt(savedSnapshot.getGeneratedAt());
		exportJob.setCompletedAt(savedSnapshot.getGeneratedAt());
		exportJob.setOutputLocation("/api/reports/" + reportCode);
		reportExportJobRepository.save(exportJob);
	}
}
