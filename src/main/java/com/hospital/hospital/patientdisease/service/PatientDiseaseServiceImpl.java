package com.hospital.hospital.patientdisease.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.common.exception.DuplicateResourceException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.disease.model.Disease;
import com.hospital.hospital.disease.repository.DiseaseRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;
import com.hospital.hospital.patientdisease.dto.CreatePatientDiseaseRequest;
import com.hospital.hospital.patientdisease.dto.PatientDiseaseResponse;
import com.hospital.hospital.patientdisease.dto.UpdatePatientDiseaseRequest;
import com.hospital.hospital.patientdisease.mapper.PatientDiseaseMapper;
import com.hospital.hospital.patientdisease.model.PatientDisease;
import com.hospital.hospital.patientdisease.repository.PatientDiseaseRepository;

/*
- Bu sınıf hasta-hastalık geçmişi iş kurallarını uygular.
- Aynı patient ile aynı disease ilişkisinin birden fazla kez açılmaması burada korunur.
- İlişki tablosu kullanıldığı için hasta ve hastalık kayıtlarının varlığı her işlemde ayrıca doğrulanır.
*/
@Service
public class PatientDiseaseServiceImpl implements PatientDiseaseService {

	private final PatientDiseaseRepository patientDiseaseRepository;
	private final PatientRepository patientRepository;
	private final DiseaseRepository diseaseRepository;
	private final PatientDiseaseMapper patientDiseaseMapper;

	public PatientDiseaseServiceImpl(
			PatientDiseaseRepository patientDiseaseRepository,
			PatientRepository patientRepository,
			DiseaseRepository diseaseRepository,
			PatientDiseaseMapper patientDiseaseMapper) {
		this.patientDiseaseRepository = patientDiseaseRepository;
		this.patientRepository = patientRepository;
		this.diseaseRepository = diseaseRepository;
		this.patientDiseaseMapper = patientDiseaseMapper;
	}

	@Override
	@Transactional
	// Yeni ilişki kaydı açılırken aynı patient-disease eşleşmesinin daha önce var olup olmadığı kontrol edilir.
	@Audit(action = "CREATE_PATIENT_DISEASE", entity = "PATIENT_DISEASE", description = "Patient disease history creation")
	public PatientDiseaseResponse create(CreatePatientDiseaseRequest request) {
		validateDuplicate(request.getPatientId(), request.getDiseaseId());
		PatientDisease patientDisease = patientDiseaseMapper.toEntity(request);
		patientDisease.setPatient(getPatient(request.getPatientId()));
		patientDisease.setDisease(getDisease(request.getDiseaseId()));
		return patientDiseaseMapper.toResponse(patientDiseaseRepository.save(patientDisease));
	}

	@Override
	@Transactional
	// Güncelleme akışında mevcut ilişki kaydı bulunur ve yeni patient/disease kombinasyonu duplicate üretmiyorsa güncellenir.
	@Audit(action = "UPDATE_PATIENT_DISEASE", entity = "PATIENT_DISEASE", description = "Patient disease history update")
	public PatientDiseaseResponse update(UUID id, UpdatePatientDiseaseRequest request) {
		PatientDisease patientDisease = getPatientDisease(id);
		validateDuplicateForUpdate(id, request.getPatientId(), request.getDiseaseId());
		patientDiseaseMapper.updateEntity(request, patientDisease);
		patientDisease.setPatient(getPatient(request.getPatientId()));
		patientDisease.setDisease(getDisease(request.getDiseaseId()));
		return patientDiseaseMapper.toResponse(patientDiseaseRepository.save(patientDisease));
	}

	@Override
	@Transactional(readOnly = true)
	// Tekil ilişki kaydını bulup response modeline dönüştürür.
	public PatientDiseaseResponse getById(UUID id) {
		return patientDiseaseMapper.toResponse(getPatientDisease(id));
	}

	@Override
	@Transactional(readOnly = true)
	// Tüm hasta-hastalık geçmiş kayıtlarını sayfalı şekilde listeler.
	public Page<PatientDiseaseResponse> getAll(Pageable pageable) {
		return patientDiseaseRepository.findAll(pageable).map(patientDiseaseMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Hastaya göre filtrelemede önce patient kaydı doğrulanır; böylece hatalı id sessizce boş listeye düşmez.
	public Page<PatientDiseaseResponse> getAllByPatient(UUID patientId, Pageable pageable) {
		getPatient(patientId);
		return patientDiseaseRepository.findAllByPatientId(patientId, pageable).map(patientDiseaseMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Hastalığa göre filtrelemede disease kaydı doğrulanır ve ilişkili geçmiş kayıtları getirilir.
	public Page<PatientDiseaseResponse> getAllByDisease(UUID diseaseId, Pageable pageable) {
		getDisease(diseaseId);
		return patientDiseaseRepository.findAllByDiseaseId(diseaseId, pageable).map(patientDiseaseMapper::toResponse);
	}

	// İlişki kaydını tek noktadan bulur; bulunamazsa ortak not found hatası üretir.
	private PatientDisease getPatientDisease(UUID id) {
		return patientDiseaseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient disease history not found: " + id));
	}

	// Patient ilişkisinin gerçekten var olup olmadığını doğrular.
	private Patient getPatient(UUID id) {
		return patientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
	}

	// Disease ilişkisinin gerçekten var olup olmadığını doğrular.
	private Disease getDisease(UUID id) {
		return diseaseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Disease not found: " + id));
	}

	// Aynı hasta ile aynı hastalık için ikinci bir kayıt açılmasını engeller.
	private void validateDuplicate(UUID patientId, UUID diseaseId) {
		if (patientDiseaseRepository.existsByPatientIdAndDiseaseId(patientId, diseaseId)) {
			throw new DuplicateResourceException("Patient disease history already exists for patient and disease");
		}
	}

	// Güncelleme sırasında duplicate kontrolü yapılır; mevcut kayıt aynı kombinasyona zaten sahipse hata verilmez.
	private void validateDuplicateForUpdate(UUID id, UUID patientId, UUID diseaseId) {
		if (!patientDiseaseRepository.existsByPatientIdAndDiseaseId(patientId, diseaseId)) {
			return;
		}
		PatientDisease existing = patientDiseaseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient disease history not found: " + id));
		if (!existing.getPatient().getId().equals(patientId) || !existing.getDisease().getId().equals(diseaseId)) {
			throw new DuplicateResourceException("Patient disease history already exists for patient and disease");
		}
	}
}
