package com.hospital.hospital.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.hospital.hospital.payment.model.Currency;
import com.hospital.hospital.payment.model.PaymentMethod;
import com.hospital.hospital.payment.model.PaymentStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UpdatePaymentRequest {

	@NotNull
	private UUID patientId;

	@NotNull
	private UUID encounterId;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal amount;

	@NotNull
	private Currency currency;

	@NotNull
	private PaymentMethod paymentMethod;

	@NotNull
	private PaymentStatus paymentStatus;

	private Instant paidAt;

	public UpdatePaymentRequest() {
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public PaymentStatus getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(PaymentStatus paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Instant getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(Instant paidAt) {
		this.paidAt = paidAt;
	}
}
