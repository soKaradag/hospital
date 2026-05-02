package com.hospital.hospital.disease.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.disease.dto.CreateDiseaseRequest;
import com.hospital.hospital.disease.dto.DiseaseResponse;
import com.hospital.hospital.disease.dto.UpdateDiseaseRequest;
import com.hospital.hospital.disease.mapper.DiseaseMapper;
import com.hospital.hospital.disease.model.Disease;
import com.hospital.hospital.disease.model.DiseaseCategory;
import com.hospital.hospital.disease.model.DiseaseCodeMapping;
import com.hospital.hospital.disease.repository.DiseaseCategoryRepository;
import com.hospital.hospital.disease.repository.DiseaseCodeMappingRepository;
import com.hospital.hospital.disease.repository.DiseaseRepository;

/*
- Bu sınıf hastalık katalogu iş kurallarını uygular.
- Hastalık code alanının benzersiz kalması burada korunur.
- Mapper ile entity ve DTO ayrımı sürdürülür; repository sadece veri erişiminde kullanılır.
*/
@Service
public class DiseaseServiceImpl implements DiseaseService {

	private static final String DEFAULT_CATEGORY_CODE = "GENERAL";
	private static final String INTERNAL_CODING_SYSTEM = "INTERNAL";

	private final DiseaseRepository diseaseRepository;
	private final DiseaseMapper diseaseMapper;
	private final DiseaseCategoryRepository diseaseCategoryRepository;
	private final DiseaseCodeMappingRepository diseaseCodeMappingRepository;

	public DiseaseServiceImpl(DiseaseRepository diseaseRepository, DiseaseMapper diseaseMapper,
			DiseaseCategoryRepository diseaseCategoryRepository, DiseaseCodeMappingRepository diseaseCodeMappingRepository) {
		this.diseaseRepository = diseaseRepository;
		this.diseaseMapper = diseaseMapper;
		this.diseaseCategoryRepository = diseaseCategoryRepository;
		this.diseaseCodeMappingRepository = diseaseCodeMappingRepository;
	}

	@Override
	@Transactional
	// Yeni kayıt öncesi disease code benzersizliği kontrol edilir; böylece katalogda aynı kod iki kez oluşmaz.
	@Audit(action = "CREATE_DISEASE", entity = "DISEASE", description = "Disease catalog creation")
	public DiseaseResponse create(CreateDiseaseRequest request) {
		validateCodeForCreate(request.getCode());
		Disease disease = diseaseMapper.toEntity(request);
		disease.setCategory(getDefaultCategory());
		Disease savedDisease = diseaseRepository.save(disease);
		ensureInternalCodeMapping(savedDisease);
		return toResponse(savedDisease);
	}

	@Override
	@Transactional
	// Güncelleme sırasında aynı code başka bir kayıtta kullanılıyorsa çakışma hatası üretilir.
	@Audit(action = "UPDATE_DISEASE", entity = "DISEASE", description = "Disease catalog update")
	public DiseaseResponse update(UUID id, UpdateDiseaseRequest request) {
		Disease disease = getDisease(id);
		validateCodeForUpdate(id, request.getCode());
		diseaseMapper.updateEntity(request, disease);
		if (disease.getCategory() == null) {
			disease.setCategory(getDefaultCategory());
		}
		Disease savedDisease = diseaseRepository.save(disease);
		ensureInternalCodeMapping(savedDisease);
		return toResponse(savedDisease);
	}

	@Override
	@Transactional(readOnly = true)
	// Tekil okuma akışı, hastalık kaydını getirip response modeline dönüştürür.
	public DiseaseResponse getById(UUID id) {
		return toResponse(getDisease(id));
	}

	@Override
	@Transactional(readOnly = true)
	// Katalogdaki tüm hastalıkları sayfalı şekilde listeler.
	public Page<DiseaseResponse> getAll(Pageable pageable) {
		return diseaseRepository.findAll(pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Search boş ise tüm kayıtlar döner; dolu ise code ve name alanlarında arama yapılır.
	public Page<DiseaseResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String value = keyword.trim();
		return diseaseRepository.findAllByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(value, value, pageable)
				.map(this::toResponse);
	}

	// Hastalık kaydını tek noktadan bulur; bulunamazsa ortak not found hatası üretir.
	private Disease getDisease(UUID id) {
		return diseaseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Disease not found: " + id));
	}

	// Yeni kayıt için code alanının katalog içinde benzersiz kalmasını sağlar.
	private void validateCodeForCreate(String code) {
		if (diseaseRepository.existsByCode(code)) {
			throw new DuplicateResourceException("Disease code already exists: " + code);
		}
	}

	// Güncellemede aynı code başka bir kayda aitse duplicate hatası üretir.
	private void validateCodeForUpdate(UUID id, String code) {
		diseaseRepository.findByCode(code)
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new DuplicateResourceException("Disease code already exists: " + code);
				});
	}

	private DiseaseCategory getDefaultCategory() {
		return diseaseCategoryRepository.findByCode(DEFAULT_CATEGORY_CODE)
				.orElseThrow(() -> new ResourceNotFoundException("Disease category not found: " + DEFAULT_CATEGORY_CODE));
	}

	private void ensureInternalCodeMapping(Disease disease) {
		DiseaseCodeMapping mapping = diseaseCodeMappingRepository
				.findByDiseaseIdAndCodingSystem(disease.getId(), INTERNAL_CODING_SYSTEM)
				.orElseGet(DiseaseCodeMapping::new);
		mapping.setDisease(disease);
		mapping.setCodingSystem(INTERNAL_CODING_SYSTEM);
		mapping.setExternalCode(disease.getCode());
		mapping.setDescription(disease.getName() + " internal mapping");
		diseaseCodeMappingRepository.save(mapping);
	}

	private DiseaseResponse toResponse(Disease disease) {
		long codeMappingCount = diseaseCodeMappingRepository.countByDiseaseId(disease.getId());
		return diseaseMapper.toResponse(disease, codeMappingCount);
	}
}
