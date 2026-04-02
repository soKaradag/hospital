package com.hospital.hospital.reporting.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Native SQL aggregation sonucunda oluşan hasta bazlı ödeme özetini taşır.
public class PatientPaymentSummaryResponse {

	private UUID patientId;
	private String patientFullName;
	private long paymentCount;
	private BigDecimal totalPaidAmount;
	private Instant lastPaidAt;

	public PatientPaymentSummaryResponse() {
	}

	public PatientPaymentSummaryResponse(UUID patientId, String patientFullName, long paymentCount,
			BigDecimal totalPaidAmount, Instant lastPaidAt) {
		this.patientId = patientId;
		this.patientFullName = patientFullName;
		this.paymentCount = paymentCount;
		this.totalPaidAmount = totalPaidAmount;
		this.lastPaidAt = lastPaidAt;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public String getPatientFullName() {
		return patientFullName;
	}

	public void setPatientFullName(String patientFullName) {
		this.patientFullName = patientFullName;
	}

	public long getPaymentCount() {
		return paymentCount;
	}

	public void setPaymentCount(long paymentCount) {
		this.paymentCount = paymentCount;
	}

	public BigDecimal getTotalPaidAmount() {
		return totalPaidAmount;
	}

	public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
		this.totalPaidAmount = totalPaidAmount;
	}

	public Instant getLastPaidAt() {
		return lastPaidAt;
	}

	public void setLastPaidAt(Instant lastPaidAt) {
		this.lastPaidAt = lastPaidAt;
	}
}
