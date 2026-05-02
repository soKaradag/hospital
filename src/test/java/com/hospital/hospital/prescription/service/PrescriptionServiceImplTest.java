package com.hospital.hospital.prescription.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.inventory.dto.InventoryConsumptionResponse;
import com.hospital.hospital.inventory.exception.InventoryShortageException;
import com.hospital.hospital.inventory.exception.InventorySyncException;
import com.hospital.hospital.inventory.service.InventoryConsumptionClient;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;
import com.hospital.hospital.prescription.dto.CreatePrescriptionRequest;
import com.hospital.hospital.prescription.mapper.PrescriptionMapper;
import com.hospital.hospital.prescription.model.Medication;
import com.hospital.hospital.prescription.model.Prescription;
import com.hospital.hospital.prescription.model.PrescriptionDispense;
import com.hospital.hospital.prescription.model.PrescriptionItem;
import com.hospital.hospital.prescription.repository.MedicationRepository;
import com.hospital.hospital.prescription.repository.PrescriptionDispenseRepository;
import com.hospital.hospital.prescription.repository.PrescriptionItemRepository;
import com.hospital.hospital.prescription.repository.PrescriptionRepository;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceImplTest {

	@Mock
	private PrescriptionRepository prescriptionRepository;

	@Mock
	private EncounterRepository encounterRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private MedicationRepository medicationRepository;

	@Mock
	private PrescriptionItemRepository prescriptionItemRepository;

	@Mock
	private PrescriptionDispenseRepository prescriptionDispenseRepository;

	@Mock
	private InventoryConsumptionClient inventoryConsumptionClient;

	private PrescriptionServiceImpl prescriptionService;

	@BeforeEach
	void setUp() {
		prescriptionService = new PrescriptionServiceImpl(
				prescriptionRepository,
				encounterRepository,
				patientRepository,
				doctorRepository,
				new PrescriptionMapper(),
				medicationRepository,
				prescriptionItemRepository,
				prescriptionDispenseRepository,
				inventoryConsumptionClient);
	}

	@Test
	void createShouldMarkDispenseCompletedWhenInventoryConsumptionSucceeds() {
		CreatePrescriptionRequest request = createRequest();
		arrangeCommonCreateFlow(request);
		when(inventoryConsumptionClient.consumePrescriptionDispense(any(), any()))
				.thenReturn(new InventoryConsumptionResponse());

		prescriptionService.create(request);

		assertEquals("COMPLETED", savedDispense().getStatus());
	}

	@Test
	void createShouldMarkDispenseInventoryShortageWhenConsumptionFailsWithShortage() {
		CreatePrescriptionRequest request = createRequest();
		arrangeCommonCreateFlow(request);
		when(inventoryConsumptionClient.consumePrescriptionDispense(any(), any()))
				.thenThrow(new InventoryShortageException("no stock"));

		prescriptionService.create(request);

		assertEquals("INVENTORY_SHORTAGE", savedDispense().getStatus());
	}

	@Test
	void createShouldMarkDispensePendingInventoryWhenConsumptionSyncFails() {
		CreatePrescriptionRequest request = createRequest();
		arrangeCommonCreateFlow(request);
		when(inventoryConsumptionClient.consumePrescriptionDispense(any(), any()))
				.thenThrow(new InventorySyncException("inventory offline"));

		prescriptionService.create(request);

		assertEquals("PENDING_INVENTORY", savedDispense().getStatus());
	}

	private void arrangeCommonCreateFlow(CreatePrescriptionRequest request) {
		Patient patient = new Patient();
		patient.setId(request.getPatientId());
		patient.setFirstName("Ali");
		patient.setLastName("Yilmaz");

		Doctor doctor = new Doctor();
		doctor.setId(request.getDoctorId());
		doctor.setFirstName("Ayse");
		doctor.setLastName("Demir");

		Encounter encounter = new Encounter();
		encounter.setId(request.getEncounterId());
		encounter.setPatient(patient);
		encounter.setDoctor(doctor);

		Medication medication = new Medication();
		medication.setId(UUID.randomUUID());
		medication.setCode("GENERAL_MED");
		medication.setName("General Medication");

		when(encounterRepository.findById(request.getEncounterId())).thenReturn(Optional.of(encounter));
		when(patientRepository.findById(request.getPatientId())).thenReturn(Optional.of(patient));
		when(doctorRepository.findById(request.getDoctorId())).thenReturn(Optional.of(doctor));
		when(prescriptionRepository.save(any(Prescription.class))).thenAnswer(invocation -> {
			Prescription prescription = invocation.getArgument(0);
			if (prescription.getId() == null) {
				prescription.setId(UUID.randomUUID());
			}
			return prescription;
		});
		when(medicationRepository.findByCode("GENERAL_MED")).thenReturn(Optional.of(medication));
		when(prescriptionItemRepository.findFirstByPrescriptionId(any())).thenReturn(Optional.empty());
		when(prescriptionItemRepository.save(any(PrescriptionItem.class))).thenAnswer(invocation -> {
			PrescriptionItem item = invocation.getArgument(0);
			if (item.getId() == null) {
				item.setId(UUID.randomUUID());
			}
			return item;
		});
		when(prescriptionDispenseRepository.findFirstByPrescriptionItemId(any())).thenReturn(Optional.empty());
		when(prescriptionItemRepository.countByPrescriptionId(any())).thenReturn(1L);
		when(prescriptionDispenseRepository.countByPrescriptionItemPrescriptionId(any())).thenReturn(1L);
	}

	private PrescriptionDispense savedDispense() {
		ArgumentCaptor<PrescriptionDispense> captor = ArgumentCaptor.forClass(PrescriptionDispense.class);
		org.mockito.Mockito.verify(prescriptionDispenseRepository).save(captor.capture());
		return captor.getValue();
	}

	private CreatePrescriptionRequest createRequest() {
		CreatePrescriptionRequest request = new CreatePrescriptionRequest();
		request.setEncounterId(UUID.randomUUID());
		request.setPatientId(UUID.randomUUID());
		request.setDoctorId(UUID.randomUUID());
		request.setPrescriptionDate(LocalDate.now());
		request.setNotes("Take after meal");
		return request;
	}
}
