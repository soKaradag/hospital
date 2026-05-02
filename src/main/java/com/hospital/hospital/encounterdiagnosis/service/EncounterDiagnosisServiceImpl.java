package com.hospital.hospital.encounterdiagnosis.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.disease.model.Disease;
import com.hospital.hospital.disease.repository.DiseaseRepository;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.encounterdiagnosis.dto.CreateEncounterDiagnosisRequest;
import com.hospital.hospital.encounterdiagnosis.dto.EncounterDiagnosisResponse;
import com.hospital.hospital.encounterdiagnosis.dto.UpdateEncounterDiagnosisRequest;
import com.hospital.hospital.encounterdiagnosis.mapper.EncounterDiagnosisMapper;
import com.hospital.hospital.encounterdiagnosis.model.EncounterDiagnosis;
import com.hospital.hospital.encounterdiagnosis.model.EncounterDiagnosisHistory;
import com.hospital.hospital.encounterdiagnosis.repository.EncounterDiagnosisHistoryRepository;
import com.hospital.hospital.encounterdiagnosis.repository.EncounterDiagnosisRepository;

/*
- Bu sınıf encounter bazlı teşhis iş kurallarını uygular.
- Teşhis kaydı, var olan bir encounter ve var olan bir disease kaydı ile ilişkilendirilir.
- Böylece serbest metin yerine katalog destekli klinik teşhis modeli kurulmuş olur.
*/
@Service
public class EncounterDiagnosisServiceImpl implements EncounterDiagnosisService {

	private final EncounterDiagnosisRepository encounterDiagnosisRepository;
	private final EncounterRepository encounterRepository;
	private final DiseaseRepository diseaseRepository;
	private final EncounterDiagnosisMapper encounterDiagnosisMapper;
	private final EncounterDiagnosisHistoryRepository encounterDiagnosisHistoryRepository;

	public EncounterDiagnosisServiceImpl(
			EncounterDiagnosisRepository encounterDiagnosisRepository,
			EncounterRepository encounterRepository,
			DiseaseRepository diseaseRepository,
			EncounterDiagnosisMapper encounterDiagnosisMapper,
			EncounterDiagnosisHistoryRepository encounterDiagnosisHistoryRepository) {
		this.encounterDiagnosisRepository = encounterDiagnosisRepository;
		this.encounterRepository = encounterRepository;
		this.diseaseRepository = diseaseRepository;
		this.encounterDiagnosisMapper = encounterDiagnosisMapper;
		this.encounterDiagnosisHistoryRepository = encounterDiagnosisHistoryRepository;
	}

	@Override
	@Transactional
	// Yeni teşhis kaydı oluştururken encounter ve disease ilişkileri doğrulanır.
	@Audit(action = "CREATE_ENCOUNTER_DIAGNOSIS", entity = "ENCOUNTER_DIAGNOSIS", description = "Encounter diagnosis creation")
	public EncounterDiagnosisResponse create(CreateEncounterDiagnosisRequest request) {
		EncounterDiagnosis encounterDiagnosis = encounterDiagnosisMapper.toEntity(request);
		encounterDiagnosis.setEncounter(getEncounter(request.getEncounterId()));
		encounterDiagnosis.setDisease(getDisease(request.getDiseaseId()));
		EncounterDiagnosis savedEncounterDiagnosis = encounterDiagnosisRepository.save(encounterDiagnosis);
		appendHistory(savedEncounterDiagnosis);
		return toResponse(savedEncounterDiagnosis);
	}

	@Override
	@Transactional
	// Güncelleme akışında mevcut kayıt bulunur ve yeni ilişki alanları güvenli şekilde yeniden bağlanır.
	@Audit(action = "UPDATE_ENCOUNTER_DIAGNOSIS", entity = "ENCOUNTER_DIAGNOSIS", description = "Encounter diagnosis update")
	public EncounterDiagnosisResponse update(UUID id, UpdateEncounterDiagnosisRequest request) {
		EncounterDiagnosis encounterDiagnosis = getEncounterDiagnosis(id);
		encounterDiagnosisMapper.updateEntity(request, encounterDiagnosis);
		encounterDiagnosis.setEncounter(getEncounter(request.getEncounterId()));
		encounterDiagnosis.setDisease(getDisease(request.getDiseaseId()));
		EncounterDiagnosis savedEncounterDiagnosis = encounterDiagnosisRepository.save(encounterDiagnosis);
		appendHistory(savedEncounterDiagnosis);
		return toResponse(savedEncounterDiagnosis);
	}

	@Override
	@Transactional(readOnly = true)
	// Tekil teşhis kaydını bulup response modeline dönüştürür.
	public EncounterDiagnosisResponse getById(UUID id) {
		return toResponse(getEncounterDiagnosis(id));
	}

	@Override
	@Transactional(readOnly = true)
	// Tüm teşhis kayıtlarını sayfalı şekilde listeler.
	public Page<EncounterDiagnosisResponse> getAll(Pageable pageable) {
		return encounterDiagnosisRepository.findAll(pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Encounter bazlı filtreleme ile belirli bir muayeneye ait tüm teşhisler getirilebilir.
	public Page<EncounterDiagnosisResponse> getAllByEncounter(UUID encounterId, Pageable pageable) {
		getEncounter(encounterId);
		return encounterDiagnosisRepository.findAllByEncounterId(encounterId, pageable)
				.map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Disease bazlı filtreleme aynı teşhisin farklı encounter'larda kullanımını görmeyi sağlar.
	public Page<EncounterDiagnosisResponse> getAllByDisease(UUID diseaseId, Pageable pageable) {
		getDisease(diseaseId);
		return encounterDiagnosisRepository.findAllByDiseaseId(diseaseId, pageable)
				.map(this::toResponse);
	}

	// Encounter teşhis kaydını tek noktadan bulur; bulunamazsa ortak not found hatası üretir.
	private EncounterDiagnosis getEncounterDiagnosis(UUID id) {
		return encounterDiagnosisRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Encounter diagnosis not found: " + id));
	}

	// Encounter ilişkisinin gerçekten var olup olmadığını doğrular.
	private Encounter getEncounter(UUID id) {
		return encounterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Encounter not found: " + id));
	}

	// Disease ilişkisinin gerçekten var olup olmadığını doğrular.
	private Disease getDisease(UUID id) {
		return diseaseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Disease not found: " + id));
	}

	private void appendHistory(EncounterDiagnosis encounterDiagnosis) {
		EncounterDiagnosisHistory history = new EncounterDiagnosisHistory();
		history.setEncounterDiagnosis(encounterDiagnosis);
		history.setDisease(encounterDiagnosis.getDisease());
		history.setNotes(encounterDiagnosis.getNotes());
		history.setRevisedAt(java.time.Instant.now());
		encounterDiagnosisHistoryRepository.save(history);
	}

	private EncounterDiagnosisResponse toResponse(EncounterDiagnosis encounterDiagnosis) {
		long historyCount = encounterDiagnosisHistoryRepository.countByEncounterDiagnosisId(encounterDiagnosis.getId());
		return encounterDiagnosisMapper.toResponse(encounterDiagnosis, historyCount);
	}
}
