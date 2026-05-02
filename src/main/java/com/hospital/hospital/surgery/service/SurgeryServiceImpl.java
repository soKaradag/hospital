package com.hospital.hospital.surgery.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.department.model.Department;
import com.hospital.hospital.department.repository.DepartmentRepository;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.inventory.exception.InventoryShortageException;
import com.hospital.hospital.inventory.exception.InventorySyncException;
import com.hospital.hospital.inventory.service.InventoryConsumptionClient;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.surgery.dto.CreateOperatingRoomRequest;
import com.hospital.hospital.surgery.dto.CreateSurgeryRequestRequest;
import com.hospital.hospital.surgery.dto.CreateSurgerySupplyTemplateRequest;
import com.hospital.hospital.surgery.dto.DoctorProcedurePrivilegeResponse;
import com.hospital.hospital.surgery.dto.GrantDoctorProcedurePrivilegeRequest;
import com.hospital.hospital.surgery.dto.OperatingRoomResponse;
import com.hospital.hospital.surgery.dto.ScheduleSurgeryRequest;
import com.hospital.hospital.surgery.dto.SurgeryRequestResponse;
import com.hospital.hospital.surgery.dto.SurgeryResponse;
import com.hospital.hospital.surgery.dto.SurgerySupplyTemplateItemRequest;
import com.hospital.hospital.surgery.dto.SurgerySupplyTemplateResponse;
import com.hospital.hospital.surgery.dto.UpdateSurgeryLifecycleRequest;
import com.hospital.hospital.surgery.model.DoctorProcedurePrivilege;
import com.hospital.hospital.surgery.model.OperatingRoom;
import com.hospital.hospital.surgery.model.Surgery;
import com.hospital.hospital.surgery.model.SurgeryRequest;
import com.hospital.hospital.surgery.model.SurgeryStatusHistory;
import com.hospital.hospital.surgery.model.SurgerySupplyTemplate;
import com.hospital.hospital.surgery.model.SurgerySupplyTemplateItem;
import com.hospital.hospital.surgery.model.SurgeryTeamAssignment;
import com.hospital.hospital.surgery.repository.DoctorProcedurePrivilegeRepository;
import com.hospital.hospital.surgery.repository.OperatingRoomRepository;
import com.hospital.hospital.surgery.repository.SurgeryRepository;
import com.hospital.hospital.surgery.repository.SurgeryRequestRepository;
import com.hospital.hospital.surgery.repository.SurgeryStatusHistoryRepository;
import com.hospital.hospital.surgery.repository.SurgerySupplyTemplateRepository;
import com.hospital.hospital.surgery.repository.SurgeryTeamAssignmentRepository;

@Service
public class SurgeryServiceImpl implements SurgeryService {

	private final DepartmentRepository departmentRepository;
	private final DoctorRepository doctorRepository;
	private final EncounterRepository encounterRepository;
	private final OperatingRoomRepository operatingRoomRepository;
	private final DoctorProcedurePrivilegeRepository doctorProcedurePrivilegeRepository;
	private final SurgerySupplyTemplateRepository surgerySupplyTemplateRepository;
	private final SurgeryRequestRepository surgeryRequestRepository;
	private final SurgeryRepository surgeryRepository;
	private final SurgeryTeamAssignmentRepository surgeryTeamAssignmentRepository;
	private final SurgeryStatusHistoryRepository surgeryStatusHistoryRepository;
	private final InventoryConsumptionClient inventoryConsumptionClient;

	public SurgeryServiceImpl(
			DepartmentRepository departmentRepository,
			DoctorRepository doctorRepository,
			EncounterRepository encounterRepository,
			OperatingRoomRepository operatingRoomRepository,
			DoctorProcedurePrivilegeRepository doctorProcedurePrivilegeRepository,
			SurgerySupplyTemplateRepository surgerySupplyTemplateRepository,
			SurgeryRequestRepository surgeryRequestRepository,
			SurgeryRepository surgeryRepository,
			SurgeryTeamAssignmentRepository surgeryTeamAssignmentRepository,
			SurgeryStatusHistoryRepository surgeryStatusHistoryRepository,
			InventoryConsumptionClient inventoryConsumptionClient) {
		this.departmentRepository = departmentRepository;
		this.doctorRepository = doctorRepository;
		this.encounterRepository = encounterRepository;
		this.operatingRoomRepository = operatingRoomRepository;
		this.doctorProcedurePrivilegeRepository = doctorProcedurePrivilegeRepository;
		this.surgerySupplyTemplateRepository = surgerySupplyTemplateRepository;
		this.surgeryRequestRepository = surgeryRequestRepository;
		this.surgeryRepository = surgeryRepository;
		this.surgeryTeamAssignmentRepository = surgeryTeamAssignmentRepository;
		this.surgeryStatusHistoryRepository = surgeryStatusHistoryRepository;
		this.inventoryConsumptionClient = inventoryConsumptionClient;
	}

	@Override
	@Transactional
	public OperatingRoomResponse createOperatingRoom(CreateOperatingRoomRequest request) {
		if (operatingRoomRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
			throw new DuplicateResourceException("Operating room code already exists: " + request.getCode());
		}
		Department department = departmentRepository.findById(request.getDepartmentId())
				.orElseThrow(() -> new ResourceNotFoundException("Department not found: " + request.getDepartmentId()));
		OperatingRoom operatingRoom = new OperatingRoom();
		operatingRoom.setDepartment(department);
		operatingRoom.setCode(request.getCode().trim());
		operatingRoom.setName(request.getName().trim());
		operatingRoom.setActive(request.isActive());
		return toResponse(operatingRoomRepository.save(operatingRoom));
	}

	@Override
	@Transactional
	public DoctorProcedurePrivilegeResponse grantDoctorProcedurePrivilege(GrantDoctorProcedurePrivilegeRequest request) {
		if (doctorProcedurePrivilegeRepository.existsByDoctorIdAndProcedureCodeIgnoreCase(
				request.getDoctorId(),
				request.getProcedureCode().trim())) {
			throw new DuplicateResourceException("Doctor procedure privilege already exists for this doctor and procedure");
		}
		Doctor doctor = doctorRepository.findById(request.getDoctorId())
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + request.getDoctorId()));
		DoctorProcedurePrivilege privilege = new DoctorProcedurePrivilege();
		privilege.setDoctor(doctor);
		privilege.setProcedureCode(request.getProcedureCode().trim());
		privilege.setProcedureName(request.getProcedureName().trim());
		privilege.setActive(request.isActive());
		privilege.setGrantedAt(Instant.now());
		return toResponse(doctorProcedurePrivilegeRepository.save(privilege));
	}

	@Override
	@Transactional
	public SurgerySupplyTemplateResponse createSupplyTemplate(CreateSurgerySupplyTemplateRequest request) {
		if (surgerySupplyTemplateRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
			throw new DuplicateResourceException("Surgery supply template code already exists: " + request.getCode());
		}
		SurgerySupplyTemplate template = new SurgerySupplyTemplate();
		template.setCode(request.getCode().trim());
		template.setName(request.getName().trim());
		template.setProcedureCode(request.getProcedureCode().trim());
		template.setActive(request.isActive());
		for (SurgerySupplyTemplateItemRequest itemRequest : request.getItems()) {
			SurgerySupplyTemplateItem item = new SurgerySupplyTemplateItem();
			item.setSurgerySupplyTemplate(template);
			item.setInventoryItemCode(itemRequest.getInventoryItemCode().trim());
			item.setQuantity(itemRequest.getQuantity());
			item.setNote(trimToNull(itemRequest.getNote()));
			template.getItems().add(item);
		}
		return toResponse(surgerySupplyTemplateRepository.save(template));
	}

	@Override
	@Transactional
	public SurgeryRequestResponse createSurgeryRequest(CreateSurgeryRequestRequest request) {
		Encounter encounter = encounterRepository.findById(request.getEncounterId())
				.orElseThrow(() -> new ResourceNotFoundException("Encounter not found: " + request.getEncounterId()));
		Doctor requestedByDoctor = doctorRepository.findById(request.getRequestedByDoctorId())
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + request.getRequestedByDoctorId()));
		if (!encounter.getDoctor().getId().equals(requestedByDoctor.getId())) {
			throw new BusinessRuleViolationException("Surgery request doctor must match encounter doctor");
		}
		SurgeryRequest surgeryRequest = new SurgeryRequest();
		surgeryRequest.setEncounter(encounter);
		surgeryRequest.setRequestedByDoctor(requestedByDoctor);
		surgeryRequest.setProcedureCode(request.getProcedureCode().trim());
		surgeryRequest.setProcedureName(request.getProcedureName().trim());
		surgeryRequest.setPriority(request.getPriority().trim());
		surgeryRequest.setStatus("REQUESTED");
		surgeryRequest.setPreferredDate(request.getPreferredDate());
		surgeryRequest.setNote(trimToNull(request.getNote()));
		return toResponse(surgeryRequestRepository.save(surgeryRequest));
	}

	@Override
	@Transactional
	public SurgeryResponse scheduleSurgery(ScheduleSurgeryRequest request) {
		SurgeryRequest surgeryRequest = surgeryRequestRepository.findById(request.getSurgeryRequestId())
				.orElseThrow(() -> new ResourceNotFoundException("Surgery request not found: " + request.getSurgeryRequestId()));
		if (surgeryRepository.existsBySurgeryRequestId(surgeryRequest.getId())) {
			throw new DuplicateResourceException("A surgery has already been scheduled for this request");
		}
		Doctor primaryDoctor = doctorRepository.findById(request.getPrimaryDoctorId())
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + request.getPrimaryDoctorId()));
		DoctorProcedurePrivilege privilege = doctorProcedurePrivilegeRepository
				.findByDoctorIdAndProcedureCodeIgnoreCase(primaryDoctor.getId(), surgeryRequest.getProcedureCode())
				.orElseThrow(() -> new BusinessRuleViolationException(
						"Primary doctor does not have a privilege for the requested procedure"));
		if (!privilege.isActive()) {
			throw new BusinessRuleViolationException("Primary doctor privilege is not active for the requested procedure");
		}
		OperatingRoom operatingRoom = operatingRoomRepository.findById(request.getOperatingRoomId())
				.orElseThrow(() -> new ResourceNotFoundException("Operating room not found: " + request.getOperatingRoomId()));
		SurgerySupplyTemplate supplyTemplate = surgerySupplyTemplateRepository.findById(request.getSupplyTemplateId())
				.orElseThrow(() -> new ResourceNotFoundException("Surgery supply template not found: " + request.getSupplyTemplateId()));
		if (!supplyTemplate.isActive()) {
			throw new BusinessRuleViolationException("Surgery supply template is not active");
		}
		if (!supplyTemplate.getProcedureCode().equalsIgnoreCase(surgeryRequest.getProcedureCode())) {
			throw new BusinessRuleViolationException("Supply template procedure code must match the surgery request");
		}
		Patient patient = surgeryRequest.getEncounter().getPatient();

		Surgery surgery = new Surgery();
		surgery.setSurgeryRequest(surgeryRequest);
		surgery.setPatient(patient);
		surgery.setPrimaryDoctor(primaryDoctor);
		surgery.setOperatingRoom(operatingRoom);
		surgery.setSupplyTemplate(supplyTemplate);
		surgery.setScheduledAt(request.getScheduledAt());
		surgery.setStatus("PLANNED");
		surgery.setInventoryStatus("NOT_STARTED");
		surgery.setNote(trimToNull(request.getNote()));
		Surgery savedSurgery = surgeryRepository.save(surgery);

		SurgeryTeamAssignment assignment = new SurgeryTeamAssignment();
		assignment.setSurgery(savedSurgery);
		assignment.setDoctor(primaryDoctor);
		assignment.setRoleName("PRIMARY_SURGEON");
		surgeryTeamAssignmentRepository.save(assignment);

		SurgeryStatusHistory history = new SurgeryStatusHistory();
		history.setSurgery(savedSurgery);
		history.setStatus("PLANNED");
		history.setChangedAt(Instant.now());
		history.setNote("Initial surgery schedule created");
		surgeryStatusHistoryRepository.save(history);

		surgeryRequest.setStatus("SCHEDULED");
		surgeryRequestRepository.save(surgeryRequest);
		tryReserveSurgeryInventory(savedSurgery);
		return toResponse(savedSurgery);
	}

	@Override
	@Transactional(readOnly = true)
	public SurgeryResponse getSurgeryById(UUID id) {
		Surgery surgery = surgeryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Surgery not found: " + id));
		return toResponse(surgery);
	}

	@Override
	@Transactional
	public SurgeryResponse cancelSurgery(UUID id, UpdateSurgeryLifecycleRequest request) {
		Surgery surgery = surgeryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Surgery not found: " + id));
		if ("COMPLETED".equalsIgnoreCase(surgery.getStatus())) {
			throw new BusinessRuleViolationException("Completed surgeries cannot be cancelled");
		}
		surgery.setStatus("CANCELLED");
		surgery.setNote(trimToNull(request.getNote()));
		try {
			inventoryConsumptionClient.releaseSurgeryReservations(surgery);
			surgery.setInventoryStatus("RELEASED");
			appendStatusHistory(surgery, "CANCELLED", "Surgery cancelled and reservations released");
		} catch (InventorySyncException exception) {
			surgery.setInventoryStatus("RELEASE_PENDING");
			appendStatusHistory(surgery, "CANCELLED", "Surgery cancelled with pending inventory release");
		}
		return toResponse(surgeryRepository.save(surgery));
	}

	@Override
	@Transactional
	public SurgeryResponse completeSurgery(UUID id, UpdateSurgeryLifecycleRequest request) {
		Surgery surgery = surgeryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Surgery not found: " + id));
		if ("CANCELLED".equalsIgnoreCase(surgery.getStatus())) {
			throw new BusinessRuleViolationException("Cancelled surgeries cannot be completed");
		}
		surgery.setStatus("COMPLETED");
		surgery.setNote(trimToNull(request.getNote()));
		try {
			inventoryConsumptionClient.releaseSurgeryReservations(surgery);
			inventoryConsumptionClient.consumeSurgerySupplies(surgery);
			surgery.setInventoryStatus("CONSUMED");
			appendStatusHistory(surgery, "COMPLETED", "Surgery completed and supplies consumed");
		} catch (InventoryShortageException | InventorySyncException exception) {
			surgery.setInventoryStatus("PENDING_CONSUMPTION");
			appendStatusHistory(surgery, "COMPLETED", "Surgery completed with pending inventory consumption");
		}
		return toResponse(surgeryRepository.save(surgery));
	}

	private OperatingRoomResponse toResponse(OperatingRoom operatingRoom) {
		OperatingRoomResponse response = new OperatingRoomResponse();
		response.setId(operatingRoom.getId());
		response.setDepartmentId(operatingRoom.getDepartment().getId());
		response.setCode(operatingRoom.getCode());
		response.setName(operatingRoom.getName());
		response.setActive(operatingRoom.isActive());
		response.setCreatedAt(operatingRoom.getCreatedAt());
		response.setUpdatedAt(operatingRoom.getUpdatedAt());
		return response;
	}

	private SurgeryRequestResponse toResponse(SurgeryRequest surgeryRequest) {
		SurgeryRequestResponse response = new SurgeryRequestResponse();
		response.setId(surgeryRequest.getId());
		response.setEncounterId(surgeryRequest.getEncounter().getId());
		response.setRequestedByDoctorId(surgeryRequest.getRequestedByDoctor().getId());
		response.setProcedureCode(surgeryRequest.getProcedureCode());
		response.setProcedureName(surgeryRequest.getProcedureName());
		response.setPriority(surgeryRequest.getPriority());
		response.setStatus(surgeryRequest.getStatus());
		response.setPreferredDate(surgeryRequest.getPreferredDate());
		response.setNote(surgeryRequest.getNote());
		response.setCreatedAt(surgeryRequest.getCreatedAt());
		response.setUpdatedAt(surgeryRequest.getUpdatedAt());
		return response;
	}

	private SurgeryResponse toResponse(Surgery surgery) {
		SurgeryResponse response = new SurgeryResponse();
		response.setId(surgery.getId());
		response.setSurgeryRequestId(surgery.getSurgeryRequest().getId());
		response.setPatientId(surgery.getPatient().getId());
		response.setPrimaryDoctorId(surgery.getPrimaryDoctor().getId());
		response.setOperatingRoomId(surgery.getOperatingRoom().getId());
		response.setSupplyTemplateId(surgery.getSupplyTemplate() != null ? surgery.getSupplyTemplate().getId() : null);
		response.setScheduledAt(surgery.getScheduledAt());
		response.setStatus(surgery.getStatus());
		response.setInventoryStatus(surgery.getInventoryStatus());
		response.setNote(surgery.getNote());
		response.setTeamCount(surgeryTeamAssignmentRepository.countBySurgeryId(surgery.getId()));
		response.setHistoryCount(surgeryStatusHistoryRepository.countBySurgeryId(surgery.getId()));
		response.setCreatedAt(surgery.getCreatedAt());
		response.setUpdatedAt(surgery.getUpdatedAt());
		return response;
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isBlank() ? null : trimmed;
	}

	private void tryReserveSurgeryInventory(Surgery surgery) {
		try {
			inventoryConsumptionClient.reserveSurgerySupplies(surgery);
			surgery.setInventoryStatus("RESERVED");
			appendStatusHistory(surgery, surgery.getStatus(), "Surgery supplies reserved");
		} catch (InventoryShortageException exception) {
			surgery.setInventoryStatus("SHORTAGE");
			appendStatusHistory(surgery, surgery.getStatus(), "Surgery planned with supply shortage");
		} catch (InventorySyncException exception) {
			surgery.setInventoryStatus("PENDING");
			appendStatusHistory(surgery, surgery.getStatus(), "Surgery planned with pending inventory reservation");
		}
		surgeryRepository.save(surgery);
	}

	private void appendStatusHistory(Surgery surgery, String status, String note) {
		SurgeryStatusHistory history = new SurgeryStatusHistory();
		history.setSurgery(surgery);
		history.setStatus(status);
		history.setChangedAt(Instant.now());
		history.setNote(note);
		surgeryStatusHistoryRepository.save(history);
	}

	private DoctorProcedurePrivilegeResponse toResponse(DoctorProcedurePrivilege privilege) {
		DoctorProcedurePrivilegeResponse response = new DoctorProcedurePrivilegeResponse();
		response.setId(privilege.getId());
		response.setDoctorId(privilege.getDoctor().getId());
		response.setProcedureCode(privilege.getProcedureCode());
		response.setProcedureName(privilege.getProcedureName());
		response.setActive(privilege.isActive());
		response.setGrantedAt(privilege.getGrantedAt());
		response.setCreatedAt(privilege.getCreatedAt());
		response.setUpdatedAt(privilege.getUpdatedAt());
		return response;
	}

	private SurgerySupplyTemplateResponse toResponse(SurgerySupplyTemplate template) {
		SurgerySupplyTemplateResponse response = new SurgerySupplyTemplateResponse();
		response.setId(template.getId());
		response.setCode(template.getCode());
		response.setName(template.getName());
		response.setProcedureCode(template.getProcedureCode());
		response.setActive(template.isActive());
		response.setItems(template.getItems().stream().map(this::toResponse).toList());
		response.setCreatedAt(template.getCreatedAt());
		response.setUpdatedAt(template.getUpdatedAt());
		return response;
	}

	private SurgerySupplyTemplateResponse.Item toResponse(SurgerySupplyTemplateItem item) {
		SurgerySupplyTemplateResponse.Item response = new SurgerySupplyTemplateResponse.Item();
		response.setId(item.getId());
		response.setInventoryItemCode(item.getInventoryItemCode());
		response.setQuantity(item.getQuantity());
		response.setNote(item.getNote());
		return response;
	}
}
