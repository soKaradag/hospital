package com.hospital.hospital.payment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.payment.model.PaymentRefund;

public interface PaymentRefundRepository extends JpaRepository<PaymentRefund, UUID> {

	long countByPaymentId(UUID paymentId);

	Optional<PaymentRefund> findByPaymentId(UUID paymentId);
}
