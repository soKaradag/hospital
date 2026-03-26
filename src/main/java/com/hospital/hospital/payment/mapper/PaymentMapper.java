package com.hospital.hospital.payment.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.payment.dto.CreatePaymentRequest;
import com.hospital.hospital.payment.dto.PaymentResponse;
import com.hospital.hospital.payment.dto.UpdatePaymentRequest;
import com.hospital.hospital.payment.model.Payment;

// Payment entity ve dto dönüşümlerini manuel olarak yönetir.
@Component
public class PaymentMapper {

	public Payment toEntity(CreatePaymentRequest request) {
		if (request == null) {
			return null;
		}
		Payment payment = new Payment();
		payment.setAmount(request.getAmount());
		payment.setCurrency(request.getCurrency());
		payment.setPaymentMethod(request.getPaymentMethod());
		payment.setPaymentStatus(request.getPaymentStatus());
		payment.setPaidAt(request.getPaidAt());
		return payment;
	}

	public void updateEntity(UpdatePaymentRequest request, Payment payment) {
		if (request == null || payment == null) {
			return;
		}
		payment.setAmount(request.getAmount());
		payment.setCurrency(request.getCurrency());
		payment.setPaymentMethod(request.getPaymentMethod());
		payment.setPaymentStatus(request.getPaymentStatus());
		payment.setPaidAt(request.getPaidAt());
	}

	public PaymentResponse toResponse(Payment payment) {
		if (payment == null) {
			return null;
		}
		PaymentResponse response = new PaymentResponse();
		response.setId(payment.getId());
		if (payment.getPatient() != null) {
			response.setPatientId(payment.getPatient().getId());
			response.setPatientFullName(payment.getPatient().getFirstName() + " " + payment.getPatient().getLastName());
		}
		if (payment.getEncounter() != null) {
			response.setEncounterId(payment.getEncounter().getId());
		}
		response.setAmount(payment.getAmount());
		response.setCurrency(payment.getCurrency());
		response.setPaymentMethod(payment.getPaymentMethod());
		response.setPaymentStatus(payment.getPaymentStatus());
		response.setPaidAt(payment.getPaidAt());
		response.setCreatedAt(payment.getCreatedAt());
		response.setUpdatedAt(payment.getUpdatedAt());
		return response;
	}
}
