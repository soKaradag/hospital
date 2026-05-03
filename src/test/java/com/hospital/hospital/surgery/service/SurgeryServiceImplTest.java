package com.hospital.hospital.surgery.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.department.model.Department;
import com.hospital.hospital.department.repository.DepartmentRepository;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.inventory.dto.SurgeryInventoryReservationResponse;
import com.hospital.hospital.inventory.exception.InventorySyncException;
import com.hospital.hospital.inventory.service.InventoryConsumptionClient;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.surgery.dto.ScheduleSurgeryRequest;
import com.hospital.hospital.surgery.dto.UpdateSurgeryLifecycleRequest;
import com.hospital.hospital.surgery.model.DoctorProcedurePrivilege;
import com.hospital.hospital.surgery.model.OperatingRoom;
import com.hospital.hospital.surgery.model.Surgery;
import com.hospital.hospital.surgery.model.SurgeryRequest;
import com.hospital.hospital.surgery.model.SurgerySupplyTemplate;
import com.hospital.hospital.surgery.model.SurgerySupplyTemplateItem;
import com.hospital.hospital.surgery.repository.DoctorProcedurePrivilegeRepository;
import com.hospital.hospital.surgery.repository.OperatingRoomRepository;
import com.hospital.hospital.surgery.repository.SurgeryRepository;
import com.hospital.hospital.surgery.repository.SurgeryRequestRepository;
import com.hospital.hospital.surgery.repository.SurgeryStatusHistoryRepository;
import com.hospital.hospital.surgery.repository.SurgerySupplyTemplateRepository;
import com.hospital.hospital.surgery.repository.SurgeryTeamAssignmentRepository;

@ExtendWith(MockitoExtension.class)
class SurgeryServiceImplTest {

	@Mock
	private DepartmentRepository departmentRepository;

	@Mock
	private DoctorRepository doctorRepository;

	@Mock
	private EncounterRepository encounterRepository;

	@Mock
	private OperatingRoomRepository operatingRoomRepository;

	@Mock
	private DoctorProcedurePrivilegeRepository doctorProcedurePrivilegeRepository;

	@Mock
	private SurgerySupplyTemplateRepository surgerySupplyTemplateRepository;

	@Mock
	private SurgeryRequestRepository surgeryRequestRepository;

	@Mock
	private SurgeryRepository surgeryRepository;

	@Mock
	private SurgeryTeamAssignmentRepository surgeryTeamAssignmentRepository;

	@Mock
	private SurgeryStatusHistoryRepository surgeryStatusHistoryRepository;

	@Mock
	private InventoryConsumptionClient inventoryConsumptionClient;

	private SurgeryServiceImpl surgeryService;

	@BeforeEach
	void setUp() {
		surgeryService = new SurgeryServiceImpl(
				departmentRepository,
				doctorRepository,
				encounterRepository,
				operatingRoomRepository,
				doctorProcedurePrivilegeRepository,
				surgerySupplyTemplateRepository,
				surgeryRequestRepository,
				surgeryRepository,
				surgeryTeamAssignmentRepository,
				surgeryStatusHistoryRepository,
				inventoryConsumptionClient);
	}

	@Test
	void scheduleSurgeryShouldMarkInventoryReservedWhenReservationSucceeds() {
		UUID surgeryRequestId = UUID.randomUUID();
		UUID doctorId = UUID.randomUUID();
		UUID operatingRoomId = UUID.randomUUID();
		UUID supplyTemplateId = UUID.randomUUID();

		SurgeryRequest surgeryRequest = surgeryRequest(surgeryRequestId, doctorId, "APPEND");
		Doctor doctor = doctor(doctorId);
		DoctorProcedurePrivilege privilege = privilege(doctor, "APPEND", true);
		OperatingRoom operatingRoom = operatingRoom(operatingRoomId);
		SurgerySupplyTemplate supplyTemplate = supplyTemplate(supplyTemplateId, "APPEND");

		when(surgeryRequestRepository.findById(surgeryRequestId)).thenReturn(Optional.of(surgeryRequest));
		when(surgeryRepository.existsBySurgeryRequestId(surgeryRequestId)).thenReturn(false);
		when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
		when(doctorProcedurePrivilegeRepository.findByDoctorIdAndProcedureCodeIgnoreCase(doctorId, "APPEND"))
				.thenReturn(Optional.of(privilege));
		when(operatingRoomRepository.findById(operatingRoomId)).thenReturn(Optional.of(operatingRoom));
		when(surgerySupplyTemplateRepository.findById(supplyTemplateId)).thenReturn(Optional.of(supplyTemplate));
		when(surgeryRepository.save(any(Surgery.class))).thenAnswer(invocation -> {
			Surgery surgery = invocation.getArgument(0);
			if (surgery.getId() == null) {
				surgery.setId(UUID.randomUUID());
			}
			return surgery;
		});
		when(surgeryTeamAssignmentRepository.countBySurgeryId(any())).thenReturn(1L);
		when(surgeryStatusHistoryRepository.countBySurgeryId(any())).thenReturn(2L);
		when(inventoryConsumptionClient.reserveSurgerySupplies(any())).thenReturn(new SurgeryInventoryReservationResponse());

		ScheduleSurgeryRequest request = new ScheduleSurgeryRequest();
		request.setSurgeryRequestId(surgeryRequestId);
		request.setPrimaryDoctorId(doctorId);
		request.setOperatingRoomId(operatingRoomId);
		request.setSupplyTemplateId(supplyTemplateId);
		request.setScheduledAt(Instant.now().plusSeconds(7200));

		var response = surgeryService.scheduleSurgery(request);

		assertEquals("PLANNED", response.getStatus());
		assertEquals("RESERVED", response.getInventoryStatus());
	}

	@Test
	void cancelSurgeryShouldMarkReleasePendingWhenInventoryReleaseFails() {
		Surgery surgery = scheduledSurgery("PLANNED");
		when(surgeryRepository.findById(surgery.getId())).thenReturn(Optional.of(surgery));
		when(surgeryRepository.save(any(Surgery.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(surgeryTeamAssignmentRepository.countBySurgeryId(any())).thenReturn(1L);
		when(surgeryStatusHistoryRepository.countBySurgeryId(any())).thenReturn(2L);
		doThrow(new InventorySyncException("inventory offline")).when(inventoryConsumptionClient)
				.releaseSurgeryReservations(surgery);

		UpdateSurgeryLifecycleRequest request = new UpdateSurgeryLifecycleRequest();
		request.setNote("cancel");

		var response = surgeryService.cancelSurgery(surgery.getId(), request);

		assertEquals("CANCELLED", response.getStatus());
		assertEquals("RELEASE_PENDING", response.getInventoryStatus());
	}

	@Test
	void completeSurgeryShouldMarkConsumedWhenInventoryLifecycleSucceeds() {
		Surgery surgery = scheduledSurgery("PLANNED");
		when(surgeryRepository.findById(surgery.getId())).thenReturn(Optional.of(surgery));
		when(surgeryRepository.save(any(Surgery.class))).thenAnswer(invocation -> invocation.getArgument(0));
		when(surgeryTeamAssignmentRepository.countBySurgeryId(any())).thenReturn(1L);
		when(surgeryStatusHistoryRepository.countBySurgeryId(any())).thenReturn(3L);
		doNothing().when(inventoryConsumptionClient).consumeSurgerySupplies(surgery);

		UpdateSurgeryLifecycleRequest request = new UpdateSurgeryLifecycleRequest();
		request.setNote("complete");

		var response = surgeryService.completeSurgery(surgery.getId(), request);

		assertEquals("COMPLETED", response.getStatus());
		assertEquals("CONSUMED", response.getInventoryStatus());
		verify(inventoryConsumptionClient, never()).releaseSurgeryReservations(surgery);
		verify(inventoryConsumptionClient).consumeSurgerySupplies(surgery);
	}

	private SurgeryRequest surgeryRequest(UUID id, UUID doctorId, String procedureCode) {
		SurgeryRequest surgeryRequest = new SurgeryRequest();
		surgeryRequest.setId(id);
		surgeryRequest.setProcedureCode(procedureCode);
		surgeryRequest.setProcedureName("Appendectomy");
		surgeryRequest.setStatus("REQUESTED");
		surgeryRequest.setEncounter(encounter(doctorId));
		surgeryRequest.setRequestedByDoctor(doctor(doctorId));
		return surgeryRequest;
	}

	private Encounter encounter(UUID doctorId) {
		Encounter encounter = new Encounter();
		encounter.setId(UUID.randomUUID());
		encounter.setDoctor(doctor(doctorId));
		encounter.setPatient(patient());
		return encounter;
	}

	private Patient patient() {
		Patient patient = new Patient();
		patient.setId(UUID.randomUUID());
		patient.setFirstName("Ali");
		patient.setLastName("Veli");
		return patient;
	}

	private Doctor doctor(UUID id) {
		Doctor doctor = new Doctor();
		doctor.setId(id);
		doctor.setFirstName("Ayse");
		doctor.setLastName("Kara");
		return doctor;
	}

	private DoctorProcedurePrivilege privilege(Doctor doctor, String procedureCode, boolean active) {
		DoctorProcedurePrivilege privilege = new DoctorProcedurePrivilege();
		privilege.setDoctor(doctor);
		privilege.setProcedureCode(procedureCode);
		privilege.setProcedureName("Appendectomy");
		privilege.setActive(active);
		return privilege;
	}

	private OperatingRoom operatingRoom(UUID id) {
		OperatingRoom room = new OperatingRoom();
		room.setId(id);
		room.setDepartment(new Department());
		room.setCode("OR-1");
		room.setName("Main OR");
		room.setActive(true);
		return room;
	}

	private SurgerySupplyTemplate supplyTemplate(UUID id, String procedureCode) {
		SurgerySupplyTemplate template = new SurgerySupplyTemplate();
		template.setId(id);
		template.setCode("TPL-1");
		template.setName("Appendectomy Kit");
		template.setProcedureCode(procedureCode);
		template.setActive(true);
		SurgerySupplyTemplateItem item = new SurgerySupplyTemplateItem();
		item.setSurgerySupplyTemplate(template);
		item.setInventoryItemCode("GENERAL_MED");
		item.setQuantity(new BigDecimal("2"));
		template.getItems().add(item);
		return template;
	}

	private Surgery scheduledSurgery(String status) {
		Surgery surgery = new Surgery();
		surgery.setId(UUID.randomUUID());
		surgery.setStatus(status);
		surgery.setInventoryStatus("RESERVED");
		surgery.setSurgeryRequest(surgeryRequest(UUID.randomUUID(), UUID.randomUUID(), "APPEND"));
		surgery.setPatient(patient());
		surgery.setPrimaryDoctor(doctor(UUID.randomUUID()));
		surgery.setOperatingRoom(operatingRoom(UUID.randomUUID()));
		surgery.setSupplyTemplate(supplyTemplate(UUID.randomUUID(), "APPEND"));
		surgery.setScheduledAt(Instant.now().plusSeconds(3600));
		return surgery;
	}
}
