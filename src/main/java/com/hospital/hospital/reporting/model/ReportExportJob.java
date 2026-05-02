package com.hospital.hospital.reporting.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_export_jobs")
public class ReportExportJob extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "report_snapshot_id", nullable = false)
	private ReportSnapshot reportSnapshot;

	@Column(name = "export_format", nullable = false, length = 20)
	private String exportFormat;

	@Column(name = "status", nullable = false, length = 30)
	private String status;

	@Column(name = "requested_at", nullable = false)
	private Instant requestedAt;

	@Column(name = "completed_at")
	private Instant completedAt;

	@Column(name = "output_location", length = 255)
	private String outputLocation;

	public ReportSnapshot getReportSnapshot() {
		return reportSnapshot;
	}

	public void setReportSnapshot(ReportSnapshot reportSnapshot) {
		this.reportSnapshot = reportSnapshot;
	}

	public String getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(String exportFormat) {
		this.exportFormat = exportFormat;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Instant getRequestedAt() {
		return requestedAt;
	}

	public void setRequestedAt(Instant requestedAt) {
		this.requestedAt = requestedAt;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Instant completedAt) {
		this.completedAt = completedAt;
	}

	public String getOutputLocation() {
		return outputLocation;
	}

	public void setOutputLocation(String outputLocation) {
		this.outputLocation = outputLocation;
	}
}
