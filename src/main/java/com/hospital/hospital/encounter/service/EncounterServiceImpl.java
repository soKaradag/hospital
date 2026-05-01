package com.hospital.hospital.encounter.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.appointment.model.Appointment;
import com.hospital.hospital.appointment.repository.AppointmentRepository;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.encounter.dto.CreateEncounterRequest;
import com.hospital.hospital.encounter.dto.EncounterResponse;
import com.hospital.hospital.encounter.dto.UpdateEncounterRequest;
import com.hospital.hospital.encounter.mapper.EncounterMapper;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.model.EncounterProcedure;
import com.hospital.hospital.encounter.model.EncounterVital;
import com.hospital.hospital.encounter.repository.EncounterProcedureRepository;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.encounter.repository.EncounterVitalRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;

@Service
public class EncounterServiceImpl implements EncounterService {

	private final EncounterRepository encounterRepository;
	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;
	private final EncounterMapper encounterMapper;
	private final EncounterVitalRepository encounterVitalRepository;
	private final EncounterProcedureRepository encounterProcedureRepository;

	public EncounterServiceImpl(EncounterRepository encounterRepository, AppointmentRepository appointmentRepository,
			PatientRepository patientRepository, DoctorRepository doctorRepository, EncounterMapper encounterMapper,
			EncounterVitalRepository encounterVitalRepository, EncounterProcedureRepository encounterProcedureRepository) {
		this.encounterRepository = encounterRepository;
		this.appointmentRepository = appointmentRepository;
		this.patientRepository = patientRepository;
		this.doctorRepository = doctorRepository;
		this.encounterMapper = encounterMapper;
		this.encounterVitalRepository = encounterVitalRepository;
		this.encounterProcedureRepository = encounterProcedureRepository;
	}

	@Override
	@Transactional
	public EncounterResponse create(CreateEncounterRequest request) {
		Encounter encounter = encounterMapper.toEntity(request);
		Appointment appointment = getOptionalAppointment(request.getAppointmentId());
		Patient patient = getPatient(request.getPatientId());
		Doctor doctor = getDoctor(request.getDoctorId());
		validateEncounterRelations(appointment, patient, doctor);
		encounter.setAppointment(appointment);
		encounter.setPatient(patient);
		encounter.setDoctor(doctor);
		Encounter savedEncounter = encounterRepository.save(encounter);
		syncDefaultClinicalDetails(savedEncounter);
		return toResponse(savedEncounter);
	}

	@Override
	@Transactional
	public EncounterResponse update(UUID id, UpdateEncounterRequest request) {
		Encounter encounter = getEncounter(id);
		Appointment appointment = getOptionalAppointment(request.getAppointmentId());
		Patient patient = getPatient(request.getPatientId());
		Doctor doctor = getDoctor(request.getDoctorId());
		validateEncounterRelations(appointment, patient, doctor);
		encounterMapper.updateEntity(request, encounter);
		encounter.setAppointment(appointment);
		encounter.setPatient(patient);
		encounter.setDoctor(doctor);
		Encounter savedEncounter = encounterRepository.save(encounter);
		syncDefaultClinicalDetails(savedEncounter);
		return toResponse(savedEncounter);
	}

	@Override
	@Transactional(readOnly = true)
	public EncounterResponse getById(UUID id) {
		return toResponse(getEncounter(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EncounterResponse> getAll(Pageable pageable) {
		return encounterRepository.findAll(pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EncounterResponse> getAllByPatient(UUID patientId, Pageable pageable) {
		getPatient(patientId);
		return encounterRepository.findAllByPatientId(patientId, pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EncounterResponse> getAllByDoctor(UUID doctorId, Pageable pageable) {
		getDoctor(doctorId);
		return encounterRepository.findAllByDoctorId(doctorId, pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EncounterResponse> getAllByDateRange(Instant startInclusive, Instant endInclusive, Pageable pageable) {
		return encounterRepository.findAllByEncounterDateTimeBetween(startInclusive, endInclusive, pageable)
				.map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<EncounterResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String value = keyword.trim();
		return encounterRepository
				.findAllByComplaintContainingIgnoreCaseOrDiagnosisNoteContainingIgnoreCaseOrTreatmentNoteContainingIgnoreCase(
						value, value, value, pageable)
				.map(this::toResponse);
	}

	private Encounter getEncounter(UUID id) {
		return encounterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Encounter not found: " + id));
	}

	private Appointment getOptionalAppointment(UUID id) {
		if (id == null) {
			return null;
		}
		return appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
	}

	private Patient getPatient(UUID id) {
		return patientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
	}

	private Doctor getDoctor(UUID id) {
		return doctorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
	}

	private void validateEncounterRelations(Appointment appointment, Patient patient, Doctor doctor) {
		if (appointment == null) {
			return;
		}
		if (!appointment.getPatient().getId().equals(patient.getId())) {
			throw new BusinessRuleViolationException("Encounter patient must match appointment patient");
		}
		if (!appointment.getDoctor().getId().equals(doctor.getId())) {
			throw new BusinessRuleViolationException("Encounter doctor must match appointment doctor");
		}
	}

	private void syncDefaultClinicalDetails(Encounter encounter) {
		syncDefaultVital(encounter);
		syncConsultationProcedure(encounter);
	}

	private void syncDefaultVital(Encounter encounter) {
		if (encounter.getComplaint() == null || encounter.getComplaint().isBlank()) {
			return;
		}
		EncounterVital vital = new EncounterVital();
		vital.setEncounter(encounter);
		vital.setVitalType("TRIAGE_NOTE");
		vital.setVitalValue(encounter.getComplaint());
		vital.setMeasuredAt(encounter.getEncounterDateTime());
		vital.setNote("Derived from encounter complaint");
		encounterVitalRepository.save(vital);
	}

	private void syncConsultationProcedure(Encounter encounter) {
		EncounterProcedure procedure = encounterProcedureRepository
				.findByEncounterIdAndProcedureCode(encounter.getId(), "CONSULTATION")
				.orElseGet(EncounterProcedure::new);
		procedure.setEncounter(encounter);
		procedure.setProcedureCode("CONSULTATION");
		procedure.setProcedureName("Consultation");
		procedure.setPerformedAt(encounter.getEncounterDateTime());
		procedure.setNote(encounter.getTreatmentNote());
		encounterProcedureRepository.save(procedure);
	}

	private EncounterResponse toResponse(Encounter encounter) {
		long vitalCount = encounterVitalRepository.countByEncounterId(encounter.getId());
		long procedureCount = encounterProcedureRepository.countByEncounterId(encounter.getId());
		return encounterMapper.toResponse(encounter, vitalCount, procedureCount);
	}
}
