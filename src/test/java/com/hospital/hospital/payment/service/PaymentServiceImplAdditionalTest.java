package com.hospital.hospital.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;
import com.hospital.hospital.payment.dto.CreatePaymentRequest;
import com.hospital.hospital.payment.dto.PaymentResponse;
import com.hospital.hospital.payment.mapper.PaymentMapper;
import com.hospital.hospital.payment.model.Currency;
import com.hospital.hospital.payment.model.Payment;
import com.hospital.hospital.payment.model.PaymentMethod;
import com.hospital.hospital.payment.model.PaymentStatus;
import com.hospital.hospital.payment.repository.PaymentRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplAdditionalTest {

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
	void createShouldSavePaymentWhenPatientMatchesEncounter() {
		UUID patientId = UUID.randomUUID();
		UUID encounterId = UUID.randomUUID();

		CreatePaymentRequest request = new CreatePaymentRequest();
		request.setPatientId(patientId);
		request.setEncounterId(encounterId);
		request.setCurrency(Currency.TRY);
		request.setPaymentMethod(PaymentMethod.CARD);
		request.setPaymentStatus(PaymentStatus.PAID);

		Patient patient = new Patient();
		patient.setId(patientId);
		Encounter encounter = new Encounter();
		encounter.setId(encounterId);
		encounter.setPatient(patient);

		Payment mapped = new Payment();
		Payment saved = new Payment();
		PaymentResponse response = new PaymentResponse();

		when(paymentMapper.toEntity(request)).thenReturn(mapped);
		when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
		when(encounterRepository.findById(encounterId)).thenReturn(Optional.of(encounter));
		when(paymentRepository.save(mapped)).thenReturn(saved);
		when(paymentMapper.toResponse(saved)).thenReturn(response);

		PaymentResponse actual = paymentService.create(request);
		assertNotNull(actual);
		assertEquals(patient, mapped.getPatient());
		assertEquals(encounter, mapped.getEncounter());
		verify(paymentRepository).save(mapped);
	}

	@Test
	void getAllByEncounterShouldReturnPagedPayments() {
		UUID encounterId = UUID.randomUUID();
		PageRequest pageable = PageRequest.of(0, 10);
		Encounter encounter = new Encounter();
		encounter.setId(encounterId);
		Payment payment = new Payment();
		Page<Payment> page = new PageImpl<>(List.of(payment), pageable, 1);

		when(encounterRepository.findById(encounterId)).thenReturn(Optional.of(encounter));
		when(paymentRepository.findAllByEncounterId(encounterId, pageable)).thenReturn(page);
		when(paymentMapper.toResponse(payment)).thenReturn(new PaymentResponse());

		Page<PaymentResponse> result = paymentService.getAllByEncounter(encounterId, pageable);
		assertEquals(1, result.getTotalElements());
	}
}
