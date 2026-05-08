package com.hospital.hospital.payment.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;
import com.hospital.hospital.payment.dto.CreatePaymentRequest;
import com.hospital.hospital.payment.dto.PaymentResponse;
import com.hospital.hospital.payment.dto.UpdatePaymentRequest;
import com.hospital.hospital.payment.mapper.PaymentMapper;
import com.hospital.hospital.payment.model.Invoice;
import com.hospital.hospital.payment.model.Payment;
import com.hospital.hospital.payment.model.PaymentRefund;
import com.hospital.hospital.payment.model.PaymentTransaction;
import com.hospital.hospital.payment.repository.PaymentRepository;
import com.hospital.hospital.payment.repository.InvoiceRepository;
import com.hospital.hospital.payment.repository.PaymentRefundRepository;
import com.hospital.hospital.payment.repository.PaymentTransactionRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final PatientRepository patientRepository;
	private final EncounterRepository encounterRepository;
	private final PaymentMapper paymentMapper;
	private final InvoiceRepository invoiceRepository;
	private final PaymentTransactionRepository paymentTransactionRepository;
	private final PaymentRefundRepository paymentRefundRepository;

	public PaymentServiceImpl(PaymentRepository paymentRepository, PatientRepository patientRepository,
			EncounterRepository encounterRepository, PaymentMapper paymentMapper,
			InvoiceRepository invoiceRepository, PaymentTransactionRepository paymentTransactionRepository,
			PaymentRefundRepository paymentRefundRepository) {
		this.paymentRepository = paymentRepository;
		this.patientRepository = patientRepository;
		this.encounterRepository = encounterRepository;
		this.paymentMapper = paymentMapper;
		this.invoiceRepository = invoiceRepository;
		this.paymentTransactionRepository = paymentTransactionRepository;
		this.paymentRefundRepository = paymentRefundRepository;
	}

	@Override
	@Transactional
	@Audit(action = "CREATE_PAYMENT", entity = "PAYMENT", description = "Payment creation")
	public PaymentResponse create(CreatePaymentRequest request) {
		Payment payment = paymentMapper.toEntity(request);
		Patient patient = getPatient(request.getPatientId());
		Encounter encounter = getEncounter(request.getEncounterId());
		validatePaymentRelations(patient, encounter);
		payment.setPatient(patient);
		payment.setEncounter(encounter);
		Payment savedPayment = paymentRepository.save(payment);
		syncFinancialRecords(savedPayment);
		return toResponse(savedPayment);
	}

	@Override
	@Transactional
	@Audit(action = "UPDATE_PAYMENT", entity = "PAYMENT", description = "Payment update")
	public PaymentResponse update(UUID id, UpdatePaymentRequest request) {
		Payment payment = getPayment(id);
		Patient patient = getPatient(request.getPatientId());
		Encounter encounter = getEncounter(request.getEncounterId());
		validatePaymentRelations(patient, encounter);
		paymentMapper.updateEntity(request, payment);
		payment.setPatient(patient);
		payment.setEncounter(encounter);
		Payment savedPayment = paymentRepository.save(payment);
		syncFinancialRecords(savedPayment);
		return toResponse(savedPayment);
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentResponse getById(UUID id) {
		return toResponse(getPayment(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentResponse> getAll(Pageable pageable) {
		return paymentRepository.findAll(pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentResponse> getAllByPatient(UUID patientId, Pageable pageable) {
		getPatient(patientId);
		return paymentRepository.findAllByPatientId(patientId, pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentResponse> getAllByEncounter(UUID encounterId, Pageable pageable) {
		getEncounter(encounterId);
		return paymentRepository.findAllByEncounterId(encounterId, pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentResponse> getAllByPaidAtRange(Instant startInclusive, Instant endInclusive, Pageable pageable) {
		return paymentRepository.findAllByPaidAtBetween(startInclusive, endInclusive, pageable)
				.map(this::toResponse);
	}

	private Payment getPayment(UUID id) {
		return paymentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
	}

	private Patient getPatient(UUID id) {
		return patientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
	}

	private Encounter getEncounter(UUID id) {
		return encounterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Encounter not found: " + id));
	}

	private void validatePaymentRelations(Patient patient, Encounter encounter) {
		if (!encounter.getPatient().getId().equals(patient.getId())) {
			throw new BusinessRuleViolationException("Payment patient must match encounter patient");
		}
	}

	private void syncFinancialRecords(Payment payment) {
		syncInvoice(payment);
		syncTransaction(payment);
		syncRefund(payment);
	}

	private void syncInvoice(Payment payment) {
		Invoice invoice = invoiceRepository.findByPaymentId(payment.getId()).orElseGet(Invoice::new);
		invoice.setPayment(payment);
		invoice.setInvoiceNumber("INV-" + payment.getId().toString().replace("-", ""));
		invoice.setIssuedAt(payment.getPaidAt() != null ? payment.getPaidAt() : payment.getCreatedAt());
		invoice.setTotalAmount(payment.getAmount());
		invoice.setCurrency(payment.getCurrency());
		invoice.setStatus(payment.getPaymentStatus().name());
		invoiceRepository.save(invoice);
	}

	private void syncTransaction(Payment payment) {
		PaymentTransaction transaction = paymentTransactionRepository.findByPaymentId(payment.getId())
				.orElseGet(PaymentTransaction::new);
		transaction.setPayment(payment);
		transaction.setTransactionReference("TXN-" + payment.getId().toString().replace("-", ""));
		transaction.setProcessedAt(payment.getPaidAt() != null ? payment.getPaidAt() : payment.getCreatedAt());
		transaction.setAmount(payment.getAmount());
		transaction.setStatus(payment.getPaymentStatus().name());
		transaction.setChannel(payment.getPaymentMethod().name());
		paymentTransactionRepository.save(transaction);
	}

	private void syncRefund(Payment payment) {
		if (payment.getPaymentStatus() != com.hospital.hospital.payment.model.PaymentStatus.CANCELLED) {
			return;
		}
		PaymentRefund refund = paymentRefundRepository.findByPaymentId(payment.getId()).orElseGet(PaymentRefund::new);
		refund.setPayment(payment);
		refund.setRefundedAt(java.time.Instant.now());
		refund.setAmount(payment.getAmount());
		refund.setReason("Cancelled payment refund placeholder");
		refund.setStatus("REQUESTED");
		paymentRefundRepository.save(refund);
	}

	private PaymentResponse toResponse(Payment payment) {
		long invoiceCount = invoiceRepository.countByPaymentId(payment.getId());
		long transactionCount = paymentTransactionRepository.countByPaymentId(payment.getId());
		long refundCount = paymentRefundRepository.countByPaymentId(payment.getId());
		return paymentMapper.toResponse(payment, invoiceCount, transactionCount, refundCount);
	}
}
