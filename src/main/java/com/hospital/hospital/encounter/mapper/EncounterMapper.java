package com.hospital.hospital.encounter.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.encounter.dto.CreateEncounterRequest;
import com.hospital.hospital.encounter.dto.EncounterResponse;
import com.hospital.hospital.encounter.dto.UpdateEncounterRequest;
import com.hospital.hospital.encounter.model.Encounter;

// Encounter entity ve dto dönüşümlerini manuel olarak yönetir.
@Component
public class EncounterMapper {

	public Encounter toEntity(CreateEncounterRequest request) {
		if (request == null) {
			return null;
		}
		Encounter encounter = new Encounter();
		encounter.setComplaint(request.getComplaint());
		encounter.setDiagnosisNote(request.getDiagnosisNote());
		encounter.setTreatmentNote(request.getTreatmentNote());
		encounter.setEncounterDateTime(request.getEncounterDateTime());
		return encounter;
	}

	public void updateEntity(UpdateEncounterRequest request, Encounter encounter) {
		if (request == null || encounter == null) {
			return;
		}
		encounter.setComplaint(request.getComplaint());
		encounter.setDiagnosisNote(request.getDiagnosisNote());
		encounter.setTreatmentNote(request.getTreatmentNote());
		encounter.setEncounterDateTime(request.getEncounterDateTime());
	}

	public EncounterResponse toResponse(Encounter encounter) {
		return toResponse(encounter, 0L, 0L);
	}

	public EncounterResponse toResponse(Encounter encounter, long vitalCount, long procedureCount) {
		if (encounter == null) {
			return null;
		}
		EncounterResponse response = new EncounterResponse();
		response.setId(encounter.getId());
		if (encounter.getAppointment() != null) {
			response.setAppointmentId(encounter.getAppointment().getId());
		}
		if (encounter.getPatient() != null) {
			response.setPatientId(encounter.getPatient().getId());
			response.setPatientFullName(encounter.getPatient().getFirstName() + " " + encounter.getPatient().getLastName());
		}
		if (encounter.getDoctor() != null) {
			response.setDoctorId(encounter.getDoctor().getId());
			response.setDoctorFullName(encounter.getDoctor().getFirstName() + " " + encounter.getDoctor().getLastName());
		}
		response.setComplaint(encounter.getComplaint());
		response.setDiagnosisNote(encounter.getDiagnosisNote());
		response.setTreatmentNote(encounter.getTreatmentNote());
		response.setEncounterDateTime(encounter.getEncounterDateTime());
		response.setVitalCount(vitalCount);
		response.setProcedureCount(procedureCount);
		response.setCreatedAt(encounter.getCreatedAt());
		response.setUpdatedAt(encounter.getUpdatedAt());
		return response;
	}
}
