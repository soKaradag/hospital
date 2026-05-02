package com.hospital.hospital.patientdisease.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.patientdisease.dto.CreatePatientDiseaseRequest;
import com.hospital.hospital.patientdisease.dto.PatientDiseaseResponse;
import com.hospital.hospital.patientdisease.dto.UpdatePatientDiseaseRequest;

/*
- Bu servis sözleşmesi hasta-hastalık geçmişi akışlarını tanımlar.
- Amaç, patient ile disease arasındaki çoktan çoğa ilişkiyi ayrı bir domain akışı olarak yönetmektir.
*/
public interface PatientDiseaseService {

	// Yeni hasta-hastalık geçmiş kaydı oluşturur.
	PatientDiseaseResponse create(CreatePatientDiseaseRequest request);

	// Mevcut ilişki kaydını günceller.
	PatientDiseaseResponse update(UUID id, UpdatePatientDiseaseRequest request);

	// Tekil ilişki kaydını getirir.
	PatientDiseaseResponse getById(UUID id);

	// Tüm ilişki kayıtlarını sayfalı şekilde listeler.
	Page<PatientDiseaseResponse> getAll(Pageable pageable);

	// Belirli bir hastaya ait tüm hastalık geçmişini getirir.
	Page<PatientDiseaseResponse> getAllByPatient(UUID patientId, Pageable pageable);

	// Belirli bir hastalığa bağlı hasta geçmiş kayıtlarını getirir.
	Page<PatientDiseaseResponse> getAllByDisease(UUID diseaseId, Pageable pageable);
}
