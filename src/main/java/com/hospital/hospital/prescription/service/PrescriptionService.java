package com.hospital.hospital.prescription.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.prescription.dto.CreatePrescriptionRequest;
import com.hospital.hospital.prescription.dto.PrescriptionResponse;
import com.hospital.hospital.prescription.dto.UpdatePrescriptionRequest;

/*
- Bu servis sözleşmesi reçete akışlarını tanımlar.
- Amaç, encounter sonrası oluşan reçete üst kayıtlarını ayrı bir klinik çıktı olarak yönetmektir.
*/
public interface PrescriptionService {

	// Yeni reçete kaydı oluşturur.
	PrescriptionResponse create(CreatePrescriptionRequest request);

	// Mevcut reçete kaydını günceller.
	PrescriptionResponse update(UUID id, UpdatePrescriptionRequest request);

	// Tekil reçete kaydını getirir.
	PrescriptionResponse getById(UUID id);

	// Tüm reçete kayıtlarını sayfalı şekilde listeler.
	Page<PrescriptionResponse> getAll(Pageable pageable);

	// Belirli bir encounter'a bağlı reçeteleri getirir.
	Page<PrescriptionResponse> getAllByEncounter(UUID encounterId, Pageable pageable);

	// Belirli bir hastaya ait reçeteleri getirir.
	Page<PrescriptionResponse> getAllByPatient(UUID patientId, Pageable pageable);

	// Belirli bir doktora ait reçeteleri getirir.
	Page<PrescriptionResponse> getAllByDoctor(UUID doctorId, Pageable pageable);

	// Belirli bir tarih aralığındaki reçeteleri getirir.
	Page<PrescriptionResponse> getAllByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
