package com.hospital.hospital.payment.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;
import com.hospital.hospital.payment.dto.CreatePaymentRequest;
import com.hospital.hospital.payment.mapper.PaymentMapper;
import com.hospital.hospital.payment.model.Currency;
import com.hospital.hospital.payment.model.Payment;
import com.hospital.hospital.payment.model.PaymentMethod;
import com.hospital.hospital.payment.model.PaymentStatus;
import com.hospital.hospital.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private EncounterRepository encounterRepository;

	@Mock
	private PaymentMapper paymentMapper;

	@InjectMocks
	private PaymentServiceImpl paymentService;

	@Test
	void createShouldThrowWhenPaymentPatientDoesNotMatchEncounterPatient() {
		UUID requestPatientId = UUID.randomUUID();
		UUID encounterPatientId = UUID.randomUUID();
		UUID encounterId = UUID.randomUUID();

		CreatePaymentRequest request = new CreatePaymentRequest();
		request.setPatientId(requestPatientId);
		request.setEncounterId(encounterId);
		request.setCurrency(Currency.TRY);
		request.setPaymentMethod(PaymentMethod.CARD);
		request.setPaymentStatus(PaymentStatus.PAID);

		Patient requestPatient = new Patient();
		requestPatient.setId(requestPatientId);

		Patient encounterPatient = new Patient();
		encounterPatient.setId(encounterPatientId);

		Encounter encounter = new Encounter();
		encounter.setPatient(encounterPatient);

		when(paymentMapper.toEntity(request)).thenReturn(new Payment());
		when(patientRepository.findById(requestPatientId)).thenReturn(Optional.of(requestPatient));
		when(encounterRepository.findById(encounterId)).thenReturn(Optional.of(encounter));

		assertThrows(BusinessRuleViolationException.class, () -> paymentService.create(request));
		verify(paymentRepository, never()).save(any(Payment.class));
	}
}
