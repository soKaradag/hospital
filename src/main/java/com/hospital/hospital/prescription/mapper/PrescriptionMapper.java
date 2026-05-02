package com.hospital.hospital.prescription.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.prescription.dto.CreatePrescriptionRequest;
import com.hospital.hospital.prescription.dto.PrescriptionResponse;
import com.hospital.hospital.prescription.dto.UpdatePrescriptionRequest;
import com.hospital.hospital.prescription.model.Prescription;

// Prescription entity ile request/response DTO'ları arasındaki manuel dönüşümleri yönetir.
@Component
public class PrescriptionMapper {

	// Create request içeriğinden doğrudan yazılabilir alanları yeni entity'ye taşır.
	public Prescription toEntity(CreatePrescriptionRequest request) {
		if (request == null) {
			return null;
		}
		Prescription prescription = new Prescription();
		prescription.setPrescriptionDate(request.getPrescriptionDate());
		prescription.setNotes(request.getNotes());
		return prescription;
	}

	// Update request içeriğini mevcut entity üzerine uygular.
	public void updateEntity(UpdatePrescriptionRequest request, Prescription prescription) {
		if (request == null || prescription == null) {
			return;
		}
		prescription.setPrescriptionDate(request.getPrescriptionDate());
		prescription.setNotes(request.getNotes());
	}

	// Entity alanlarını dış API için açıklayıcı response modeline dönüştürür.
	// Patient ve doctor özet alanları birlikte dönülerek istemcinin ek sorgu ihtiyacı azaltılır.
	public PrescriptionResponse toResponse(Prescription prescription) {
		return toResponse(prescription, 0L, 0L);
	}

	public PrescriptionResponse toResponse(Prescription prescription, long itemCount, long dispenseCount) {
		if (prescription == null) {
			return null;
		}
		PrescriptionResponse response = new PrescriptionResponse();
		response.setId(prescription.getId());
		if (prescription.getEncounter() != null) {
			response.setEncounterId(prescription.getEncounter().getId());
		}
		if (prescription.getPatient() != null) {
			response.setPatientId(prescription.getPatient().getId());
			response.setPatientFullName(prescription.getPatient().getFirstName() + " " + prescription.getPatient().getLastName());
		}
		if (prescription.getDoctor() != null) {
			response.setDoctorId(prescription.getDoctor().getId());
			response.setDoctorFullName(prescription.getDoctor().getFirstName() + " " + prescription.getDoctor().getLastName());
		}
		response.setPrescriptionDate(prescription.getPrescriptionDate());
		response.setNotes(prescription.getNotes());
		response.setItemCount(itemCount);
		response.setDispenseCount(dispenseCount);
		response.setCreatedAt(prescription.getCreatedAt());
		response.setUpdatedAt(prescription.getUpdatedAt());
		return response;
	}
}
