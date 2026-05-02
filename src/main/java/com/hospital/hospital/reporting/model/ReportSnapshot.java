package com.hospital.hospital.reporting.model;

import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "report_snapshots")
public class ReportSnapshot extends BaseEntity {

	@Column(name = "report_code", nullable = false, length = 100)
	private String reportCode;

	@Column(name = "generated_at", nullable = false)
	private Instant generatedAt;

	@Column(name = "row_count", nullable = false)
	private int rowCount;

	@Column(name = "snapshot_summary", length = 500)
	private String snapshotSummary;

	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public Instant getGeneratedAt() {
		return generatedAt;
	}

	public void setGeneratedAt(Instant generatedAt) {
		this.generatedAt = generatedAt;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public String getSnapshotSummary() {
		return snapshotSummary;
	}

	public void setSnapshotSummary(String snapshotSummary) {
		this.snapshotSummary = snapshotSummary;
	}
}
