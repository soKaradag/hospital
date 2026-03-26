package com.hospital.hospital.payment.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.payment.dto.CreatePaymentRequest;
import com.hospital.hospital.payment.dto.PaymentResponse;
import com.hospital.hospital.payment.dto.UpdatePaymentRequest;

public interface PaymentService {

	PaymentResponse create(CreatePaymentRequest request);

	PaymentResponse update(UUID id, UpdatePaymentRequest request);

	PaymentResponse getById(UUID id);

	Page<PaymentResponse> getAll(Pageable pageable);

	Page<PaymentResponse> getAllByPatient(UUID patientId, Pageable pageable);

	Page<PaymentResponse> getAllByEncounter(UUID encounterId, Pageable pageable);

	Page<PaymentResponse> getAllByPaidAtRange(Instant startInclusive, Instant endInclusive, Pageable pageable);
}
