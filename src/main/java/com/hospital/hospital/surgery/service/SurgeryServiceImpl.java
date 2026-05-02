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
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.surgery.dto.CreateOperatingRoomRequest;
import com.hospital.hospital.surgery.dto.CreateSurgeryRequestRequest;
import com.hospital.hospital.surgery.dto.OperatingRoomResponse;
import com.hospital.hospital.surgery.dto.ScheduleSurgeryRequest;
import com.hospital.hospital.surgery.dto.SurgeryRequestResponse;
import com.hospital.hospital.surgery.dto.SurgeryResponse;
import com.hospital.hospital.surgery.model.OperatingRoom;
import com.hospital.hospital.surgery.model.Surgery;
import com.hospital.hospital.surgery.model.SurgeryRequest;
import com.hospital.hospital.surgery.model.SurgeryStatusHistory;
import com.hospital.hospital.surgery.model.SurgeryTeamAssignment;
import com.hospital.hospital.surgery.repository.OperatingRoomRepository;
import com.hospital.hospital.surgery.repository.SurgeryRepository;
import com.hospital.hospital.surgery.repository.SurgeryRequestRepository;
import com.hospital.hospital.surgery.repository.SurgeryStatusHistoryRepository;
import com.hospital.hospital.surgery.repository.SurgeryTeamAssignmentRepository;

@Service
public class SurgeryServiceImpl implements SurgeryService {

	private final DepartmentRepository departmentRepository;
	private final DoctorRepository doctorRepository;
	private final EncounterRepository encounterRepository;
	private final OperatingRoomRepository operatingRoomRepository;
	private final SurgeryRequestRepository surgeryRequestRepository;
	private final SurgeryRepository surgeryRepository;
	private final SurgeryTeamAssignmentRepository surgeryTeamAssignmentRepository;
	private final SurgeryStatusHistoryRepository surgeryStatusHistoryRepository;

	public SurgeryServiceImpl(
			DepartmentRepository departmentRepository,
			DoctorRepository doctorRepository,
			EncounterRepository encounterRepository,
			OperatingRoomRepository operatingRoomRepository,
			SurgeryRequestRepository surgeryRequestRepository,
			SurgeryRepository surgeryRepository,
			SurgeryTeamAssignmentRepository surgeryTeamAssignmentRepository,
			SurgeryStatusHistoryRepository surgeryStatusHistoryRepository) {
		this.departmentRepository = departmentRepository;
		this.doctorRepository = doctorRepository;
		this.encounterRepository = encounterRepository;
		this.operatingRoomRepository = operatingRoomRepository;
		this.surgeryRequestRepository = surgeryRequestRepository;
		this.surgeryRepository = surgeryRepository;
		this.surgeryTeamAssignmentRepository = surgeryTeamAssignmentRepository;
		this.surgeryStatusHistoryRepository = surgeryStatusHistoryRepository;
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
		OperatingRoom operatingRoom = operatingRoomRepository.findById(request.getOperatingRoomId())
				.orElseThrow(() -> new ResourceNotFoundException("Operating room not found: " + request.getOperatingRoomId()));
		Patient patient = surgeryRequest.getEncounter().getPatient();

		Surgery surgery = new Surgery();
		surgery.setSurgeryRequest(surgeryRequest);
		surgery.setPatient(patient);
		surgery.setPrimaryDoctor(primaryDoctor);
		surgery.setOperatingRoom(operatingRoom);
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
		return toResponse(savedSurgery);
	}

	@Override
	@Transactional(readOnly = true)
	public SurgeryResponse getSurgeryById(UUID id) {
		Surgery surgery = surgeryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Surgery not found: " + id));
		return toResponse(surgery);
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
}
