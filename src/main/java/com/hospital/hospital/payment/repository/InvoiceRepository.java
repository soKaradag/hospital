package com.hospital.hospital.payment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.payment.model.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

	long countByPaymentId(UUID paymentId);

	Optional<Invoice> findByPaymentId(UUID paymentId);
}
