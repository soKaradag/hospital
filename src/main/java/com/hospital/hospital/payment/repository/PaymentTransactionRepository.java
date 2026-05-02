package com.hospital.hospital.payment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.payment.model.PaymentTransaction;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, UUID> {

	long countByPaymentId(UUID paymentId);

	Optional<PaymentTransaction> findByPaymentId(UUID paymentId);
}
