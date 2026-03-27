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
import com.hospital.hospital.disease.repository.DiseaseRepository;

/*
- Bu sınıf hastalık katalogu iş kurallarını uygular.
- Hastalık code alanının benzersiz kalması burada korunur.
- Mapper ile entity ve DTO ayrımı sürdürülür; repository sadece veri erişiminde kullanılır.
*/
@Service
public class DiseaseServiceImpl implements DiseaseService {

	private final DiseaseRepository diseaseRepository;
	private final DiseaseMapper diseaseMapper;

	public DiseaseServiceImpl(DiseaseRepository diseaseRepository, DiseaseMapper diseaseMapper) {
		this.diseaseRepository = diseaseRepository;
		this.diseaseMapper = diseaseMapper;
	}

	@Override
	@Transactional
	// Yeni kayıt öncesi disease code benzersizliği kontrol edilir; böylece katalogda aynı kod iki kez oluşmaz.
	@Audit(action = "CREATE_DISEASE", entity = "DISEASE", description = "Disease catalog creation")
	public DiseaseResponse create(CreateDiseaseRequest request) {
		validateCodeForCreate(request.getCode());
		Disease disease = diseaseMapper.toEntity(request);
		return diseaseMapper.toResponse(diseaseRepository.save(disease));
	}

	@Override
	@Transactional
	// Güncelleme sırasında aynı code başka bir kayıtta kullanılıyorsa çakışma hatası üretilir.
	@Audit(action = "UPDATE_DISEASE", entity = "DISEASE", description = "Disease catalog update")
	public DiseaseResponse update(UUID id, UpdateDiseaseRequest request) {
		Disease disease = getDisease(id);
		validateCodeForUpdate(id, request.getCode());
		diseaseMapper.updateEntity(request, disease);
		return diseaseMapper.toResponse(diseaseRepository.save(disease));
	}

	@Override
	@Transactional(readOnly = true)
	// Tekil okuma akışı, hastalık kaydını getirip response modeline dönüştürür.
	public DiseaseResponse getById(UUID id) {
		return diseaseMapper.toResponse(getDisease(id));
	}

	@Override
	@Transactional(readOnly = true)
	// Katalogdaki tüm hastalıkları sayfalı şekilde listeler.
	public Page<DiseaseResponse> getAll(Pageable pageable) {
		return diseaseRepository.findAll(pageable).map(diseaseMapper::toResponse);
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
				.map(diseaseMapper::toResponse);
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
}
