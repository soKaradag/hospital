package com.hospital.hospital.payment.model;

import java.math.BigDecimal;
import java.time.Instant;

import com.hospital.hospital.common.model.BaseEntity;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.patient.model.Patient;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payments")
public class Payment extends BaseEntity {

	public Payment() {
	}

	public Payment(Patient patient, Encounter encounter, BigDecimal amount, Currency currency,
			PaymentMethod paymentMethod, PaymentStatus paymentStatus, Instant paidAt) {
		this.patient = patient;
		this.encounter = encounter;
		this.amount = amount;
		this.currency = currency;
		this.paymentMethod = paymentMethod;
		this.paymentStatus = paymentStatus;
		this.paidAt = paidAt;
	}

	// ManyToOne burada bir hastanın sistemde birden fazla ödeme kaydına sahip olabileceğini anlatır.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	// JoinColumn ise payments tablosundaki patient_id kolonunun patients tablosuna bağlandığını belirtir.
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;

	// ManyToOne burada bir muayene kaydına bağlı birden fazla finansal hareket tanımlanabilmesine alan açar.
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	// JoinColumn ise payments tablosundaki encounter_id kolonunun encounters tablosuna bağlandığını belirtir.
	@JoinColumn(name = "encounter_id", nullable = false)
	private Encounter encounter;

	// Tutar alanı finansal hassasiyet için BigDecimal olarak tanımlandı.
	@Column(name = "amount", nullable = false, precision = 12, scale = 2)
	private BigDecimal amount;

	// Para birimi kontrollü değer seti ile yönetilsin diye enum olarak tutulur.
	@Enumerated(EnumType.STRING)
	@Column(name = "currency", nullable = false, length = 3)
	private Currency currency;

	// Ödeme yöntemi raporlama ve işlem ayrımı için enum olarak tutulur.
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method", nullable = false, length = 20)
	private PaymentMethod paymentMethod;

	// Ödeme durumu tahsilat akışını takip etmek için ayrı tutulur.
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_status", nullable = false, length = 20)
	private PaymentStatus paymentStatus;

	// Gerçek tahsilat zamanı varsa UTC olarak ayrıca kaydedilir.
	@Column(name = "paid_at")
	private Instant paidAt;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public Encounter getEncounter() {
		return encounter;
	}

	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
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
