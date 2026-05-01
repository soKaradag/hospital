package com.hospital.hospital.payment.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.hospital.hospital.payment.model.Currency;
import com.hospital.hospital.payment.model.PaymentMethod;
import com.hospital.hospital.payment.model.PaymentStatus;

public class PaymentResponse {

	private UUID id;
	private UUID patientId;
	private String patientFullName;
	private UUID encounterId;
	private BigDecimal amount;
	private Currency currency;
	private PaymentMethod paymentMethod;
	private PaymentStatus paymentStatus;
	private Instant paidAt;
	private long invoiceCount;
	private long transactionCount;
	private long refundCount;
	private Instant createdAt;
	private Instant updatedAt;

	public PaymentResponse() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
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

	public long getInvoiceCount() {
		return invoiceCount;
	}

	public void setInvoiceCount(long invoiceCount) {
		this.invoiceCount = invoiceCount;
	}

	public long getTransactionCount() {
		return transactionCount;
	}

	public void setTransactionCount(long transactionCount) {
		this.transactionCount = transactionCount;
	}

	public long getRefundCount() {
		return refundCount;
	}

	public void setRefundCount(long refundCount) {
		this.refundCount = refundCount;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
