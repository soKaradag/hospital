package com.hospital.hospital.encounter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.appointment.repository.AppointmentRepository;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.encounter.dto.CreateEncounterRequest;
import com.hospital.hospital.encounter.mapper.EncounterMapper;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.model.EncounterProcedure;
import com.hospital.hospital.encounter.repository.EncounterProcedureRepository;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.encounter.repository.EncounterVitalRepository;
import com.hospital.hospital.inventory.dto.InventoryConsumptionResponse;
import com.hospital.hospital.inventory.exception.InventorySyncException;
import com.hospital.hospital.inventory.service.InventoryConsumptionClient;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;

@ExtendWith(MockitoExtension.class)
class EncounterServiceImplTest {

	@Mock
	private EncounterRepository encounterRepository;

	@Mock
	private AppointmentRepository appointmentRepository;

	@Mock
	private PatientRepository patientRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private EncounterVitalRepository encounterVitalRepository;

	@Mock
	private EncounterProcedureRepository encounterProcedureRepository;

	@Mock
	private InventoryConsumptionClient inventoryConsumptionClient;

	private EncounterServiceImpl encounterService;

	@BeforeEach
	void setUp() {
		encounterService = new EncounterServiceImpl(
				encounterRepository,
				appointmentRepository,
				patientRepository,
				doctorRepository,
				new EncounterMapper(),
				encounterVitalRepository,
				encounterProcedureRepository,
				inventoryConsumptionClient);
	}

	@Test
	void createShouldSyncConsultationProcedureWhenInventoryIsAvailable() {
		CreateEncounterRequest request = createRequest();
		arrangeCommonCreateFlow(request);
		when(inventoryConsumptionClient.consumeEncounterProcedure(any())).thenReturn(new InventoryConsumptionResponse());
		when(encounterVitalRepository.countByEncounterId(any())).thenReturn(1L);
		when(encounterProcedureRepository.countByEncounterId(any())).thenReturn(1L);

		var response = encounterService.create(request);

		assertEquals(1L, response.getProcedureCount());
	}

	@Test
	void createShouldFailWhenProcedureInventorySyncFails() {
		CreateEncounterRequest request = createRequest();
		arrangeCommonCreateFlow(request);
		when(inventoryConsumptionClient.consumeEncounterProcedure(any()))
				.thenThrow(new InventorySyncException("inventory unavailable"));

		assertThrows(BusinessRuleViolationException.class, () -> encounterService.create(request));
	}

	private void arrangeCommonCreateFlow(CreateEncounterRequest request) {
		Patient patient = new Patient();
		patient.setId(request.getPatientId());
		patient.setFirstName("Test");
		patient.setLastName("Patient");

		Doctor doctor = new Doctor();
		doctor.setId(request.getDoctorId());
		doctor.setFirstName("Test");
		doctor.setLastName("Doctor");

		when(patientRepository.findById(request.getPatientId())).thenReturn(Optional.of(patient));
		when(doctorRepository.findById(request.getDoctorId())).thenReturn(Optional.of(doctor));
		when(encounterRepository.save(any(Encounter.class))).thenAnswer(invocation -> {
			Encounter encounter = invocation.getArgument(0);
			if (encounter.getId() == null) {
				encounter.setId(UUID.randomUUID());
			}
			return encounter;
		});
		when(encounterProcedureRepository.findByEncounterIdAndProcedureCode(any(), any())).thenReturn(Optional.empty());
		when(encounterProcedureRepository.save(any(EncounterProcedure.class))).thenAnswer(invocation -> {
			EncounterProcedure procedure = invocation.getArgument(0);
			if (procedure.getId() == null) {
				procedure.setId(UUID.randomUUID());
			}
			return procedure;
		});
	}

	private CreateEncounterRequest createRequest() {
		CreateEncounterRequest request = new CreateEncounterRequest();
		request.setPatientId(UUID.randomUUID());
		request.setDoctorId(UUID.randomUUID());
		request.setComplaint("Headache");
		request.setDiagnosisNote("Migraine");
		request.setTreatmentNote("Consultation");
		request.setEncounterDateTime(Instant.now());
		return request;
	}
}
