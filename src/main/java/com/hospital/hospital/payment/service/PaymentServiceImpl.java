package com.hospital.hospital.payment.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.hospital.hospital.payment.model.Payment;
import com.hospital.hospital.payment.repository.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final PatientRepository patientRepository;
	private final EncounterRepository encounterRepository;
	private final PaymentMapper paymentMapper;

	public PaymentServiceImpl(PaymentRepository paymentRepository, PatientRepository patientRepository,
			EncounterRepository encounterRepository, PaymentMapper paymentMapper) {
		this.paymentRepository = paymentRepository;
		this.patientRepository = patientRepository;
		this.encounterRepository = encounterRepository;
		this.paymentMapper = paymentMapper;
	}

	@Override
	@Transactional
	public PaymentResponse create(CreatePaymentRequest request) {
		Payment payment = paymentMapper.toEntity(request);
		Patient patient = getPatient(request.getPatientId());
		Encounter encounter = getEncounter(request.getEncounterId());
		validatePaymentRelations(patient, encounter);
		payment.setPatient(patient);
		payment.setEncounter(encounter);
		return paymentMapper.toResponse(paymentRepository.save(payment));
	}

	@Override
	@Transactional
	public PaymentResponse update(UUID id, UpdatePaymentRequest request) {
		Payment payment = getPayment(id);
		Patient patient = getPatient(request.getPatientId());
		Encounter encounter = getEncounter(request.getEncounterId());
		validatePaymentRelations(patient, encounter);
		paymentMapper.updateEntity(request, payment);
		payment.setPatient(patient);
		payment.setEncounter(encounter);
		return paymentMapper.toResponse(paymentRepository.save(payment));
	}

	@Override
	@Transactional(readOnly = true)
	public PaymentResponse getById(UUID id) {
		return paymentMapper.toResponse(getPayment(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentResponse> getAll(Pageable pageable) {
		return paymentRepository.findAll(pageable).map(paymentMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentResponse> getAllByPatient(UUID patientId, Pageable pageable) {
		getPatient(patientId);
		return paymentRepository.findAllByPatientId(patientId, pageable).map(paymentMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentResponse> getAllByEncounter(UUID encounterId, Pageable pageable) {
		getEncounter(encounterId);
		return paymentRepository.findAllByEncounterId(encounterId, pageable).map(paymentMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PaymentResponse> getAllByPaidAtRange(Instant startInclusive, Instant endInclusive, Pageable pageable) {
		return paymentRepository.findAllByPaidAtBetween(startInclusive, endInclusive, pageable)
				.map(paymentMapper::toResponse);
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
}
