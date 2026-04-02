package com.hospital.hospital.reporting.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

// Trigger ile üretilen payment audit kayıtlarını istemciye taşır.
public class PaymentAuditResponse {

	private UUID id;
	private UUID paymentId;
	private UUID patientId;
	private UUID encounterId;
	private String action;
	private BigDecimal amount;
	private String currency;
	private String paymentMethod;
	private String paymentStatus;
	private Instant paidAt;
	private Instant loggedAt;

	public PaymentAuditResponse() {
	}

	public PaymentAuditResponse(UUID id, UUID paymentId, UUID patientId, UUID encounterId, String action,
			BigDecimal amount, String currency, String paymentMethod, String paymentStatus, Instant paidAt,
			Instant loggedAt) {
		this.id = id;
		this.paymentId = paymentId;
		this.patientId = patientId;
		this.encounterId = encounterId;
		this.action = action;
		this.amount = amount;
		this.currency = currency;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
		this.paidAt = paidAt;
		this.loggedAt = loggedAt;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(UUID paymentId) {
		this.paymentId = paymentId;
	}

	public UUID getPatientId() {
		return patientId;
	}

	public void setPatientId(UUID patientId) {
		this.patientId = patientId;
	}

	public UUID getEncounterId() {
		return encounterId;
	}

	public void setEncounterId(UUID encounterId) {
		this.encounterId = encounterId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Instant getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(Instant paidAt) {
		this.paidAt = paidAt;
	}

	public Instant getLoggedAt() {
		return loggedAt;
	}

	public void setLoggedAt(Instant loggedAt) {
		this.loggedAt = loggedAt;
	}
}
