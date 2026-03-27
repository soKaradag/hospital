package com.hospital.hospital.encounterdiagnosis.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.encounterdiagnosis.dto.CreateEncounterDiagnosisRequest;
import com.hospital.hospital.encounterdiagnosis.dto.EncounterDiagnosisResponse;
import com.hospital.hospital.encounterdiagnosis.dto.UpdateEncounterDiagnosisRequest;

/*
- Bu servis sözleşmesi encounter bazlı teşhis akışlarını tanımlar.
- Amaç, muayene anındaki teşhisleri hasta geçmişi kayıtlarından ayrı bir uygulama akışı olarak yönetmektir.
*/
public interface EncounterDiagnosisService {

	// Yeni encounter teşhis kaydı oluşturur.
	EncounterDiagnosisResponse create(CreateEncounterDiagnosisRequest request);

	// Mevcut teşhis kaydını günceller.
	EncounterDiagnosisResponse update(UUID id, UpdateEncounterDiagnosisRequest request);

	// Tekil teşhis kaydını getirir.
	EncounterDiagnosisResponse getById(UUID id);

	// Tüm teşhis kayıtlarını sayfalı şekilde listeler.
	Page<EncounterDiagnosisResponse> getAll(Pageable pageable);

	// Belirli bir encounter'a bağlı teşhis kayıtlarını getirir.
	Page<EncounterDiagnosisResponse> getAllByEncounter(UUID encounterId, Pageable pageable);

	// Belirli bir disease kaydına bağlı encounter teşhislerini getirir.
	Page<EncounterDiagnosisResponse> getAllByDisease(UUID diseaseId, Pageable pageable);
}
