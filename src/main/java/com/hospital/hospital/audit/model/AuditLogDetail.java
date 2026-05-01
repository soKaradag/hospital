package com.hospital.hospital.audit.model;

import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_log_details")
public class AuditLogDetail extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "audit_log_id", nullable = false)
	private AuditLog auditLog;

	@Column(name = "detail_key", nullable = false, length = 100)
	private String detailKey;

	@Column(name = "detail_value", length = 500)
	private String detailValue;

	public AuditLog getAuditLog() {
		return auditLog;
	}

	public void setAuditLog(AuditLog auditLog) {
		this.auditLog = auditLog;
	}

	public String getDetailKey() {
		return detailKey;
	}

	public void setDetailKey(String detailKey) {
		this.detailKey = detailKey;
	}

	public String getDetailValue() {
		return detailValue;
	}

	public void setDetailValue(String detailValue) {
		this.detailValue = detailValue;
	}
}
