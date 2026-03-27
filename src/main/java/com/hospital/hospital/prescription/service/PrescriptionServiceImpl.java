package com.hospital.hospital.prescription.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.audit.annotation.Audit;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.encounter.model.Encounter;
import com.hospital.hospital.encounter.repository.EncounterRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;
import com.hospital.hospital.prescription.dto.CreatePrescriptionRequest;
import com.hospital.hospital.prescription.dto.PrescriptionResponse;
import com.hospital.hospital.prescription.dto.UpdatePrescriptionRequest;
import com.hospital.hospital.prescription.mapper.PrescriptionMapper;
import com.hospital.hospital.prescription.model.Prescription;
import com.hospital.hospital.prescription.repository.PrescriptionRepository;

/*
- Bu sınıf reçete iş kurallarını uygular.
- Reçete kaydı oluşturulurken patient ve doctor ilişkilerinin ilgili encounter ile uyumlu olması zorunludur.
- Böylece farklı muayeneye ait hasta veya doktor bilgisi ile tutarsız reçete kaydı oluşması engellenir.
*/
@Service
public class PrescriptionServiceImpl implements PrescriptionService {

	private final PrescriptionRepository prescriptionRepository;
	private final EncounterRepository encounterRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;
	private final PrescriptionMapper prescriptionMapper;

	public PrescriptionServiceImpl(
			PrescriptionRepository prescriptionRepository,
			EncounterRepository encounterRepository,
			PatientRepository patientRepository,
			DoctorRepository doctorRepository,
			PrescriptionMapper prescriptionMapper) {
		this.prescriptionRepository = prescriptionRepository;
		this.encounterRepository = encounterRepository;
		this.patientRepository = patientRepository;
		this.doctorRepository = doctorRepository;
		this.prescriptionMapper = prescriptionMapper;
	}

	@Override
	@Transactional
	// Yeni reçete kaydı oluştururken encounter, patient ve doctor ilişkileri yüklenir ve tutarlılık kontrolü yapılır.
	@Audit(action = "CREATE_PRESCRIPTION", entity = "PRESCRIPTION", description = "Prescription creation")
	public PrescriptionResponse create(CreatePrescriptionRequest request) {
		Prescription prescription = prescriptionMapper.toEntity(request);
		Encounter encounter = getEncounter(request.getEncounterId());
		Patient patient = getPatient(request.getPatientId());
		Doctor doctor = getDoctor(request.getDoctorId());
		validatePrescriptionRelations(encounter, patient, doctor);
		prescription.setEncounter(encounter);
		prescription.setPatient(patient);
		prescription.setDoctor(doctor);
		return prescriptionMapper.toResponse(prescriptionRepository.save(prescription));
	}

	@Override
	@Transactional
	// Güncelleme akışında mevcut reçete kaydı bulunur, yeni alanlar uygulanır ve ilişki tutarlılığı tekrar doğrulanır.
	@Audit(action = "UPDATE_PRESCRIPTION", entity = "PRESCRIPTION", description = "Prescription update")
	public PrescriptionResponse update(UUID id, UpdatePrescriptionRequest request) {
		Prescription prescription = getPrescription(id);
		Encounter encounter = getEncounter(request.getEncounterId());
		Patient patient = getPatient(request.getPatientId());
		Doctor doctor = getDoctor(request.getDoctorId());
		validatePrescriptionRelations(encounter, patient, doctor);
		prescriptionMapper.updateEntity(request, prescription);
		prescription.setEncounter(encounter);
		prescription.setPatient(patient);
		prescription.setDoctor(doctor);
		return prescriptionMapper.toResponse(prescriptionRepository.save(prescription));
	}

	@Override
	@Transactional(readOnly = true)
	// Tekil reçete kaydını bulup response modeline dönüştürür.
	public PrescriptionResponse getById(UUID id) {
		return prescriptionMapper.toResponse(getPrescription(id));
	}

	@Override
	@Transactional(readOnly = true)
	// Tüm reçete kayıtlarını sayfalı şekilde listeler.
	public Page<PrescriptionResponse> getAll(Pageable pageable) {
		return prescriptionRepository.findAll(pageable).map(prescriptionMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Encounter bazlı filtreleme belirli bir muayeneye bağlı reçeteleri getirir.
	public Page<PrescriptionResponse> getAllByEncounter(UUID encounterId, Pageable pageable) {
		getEncounter(encounterId);
		return prescriptionRepository.findAllByEncounterId(encounterId, pageable).map(prescriptionMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Patient bazlı filtreleme belirli hastaya ait reçeteleri getirir.
	public Page<PrescriptionResponse> getAllByPatient(UUID patientId, Pageable pageable) {
		getPatient(patientId);
		return prescriptionRepository.findAllByPatientId(patientId, pageable).map(prescriptionMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Doctor bazlı filtreleme belirli doktorun yazdığı reçeteleri getirir.
	public Page<PrescriptionResponse> getAllByDoctor(UUID doctorId, Pageable pageable) {
		getDoctor(doctorId);
		return prescriptionRepository.findAllByDoctorId(doctorId, pageable).map(prescriptionMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Tarih aralığı filtresi reçete raporlaması ve günlük/haftalık incelemeler için temel sağlar.
	public Page<PrescriptionResponse> getAllByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
		return prescriptionRepository.findAllByPrescriptionDateBetween(startDate, endDate, pageable)
				.map(prescriptionMapper::toResponse);
	}

	// Reçete kaydını tek noktadan bulur; bulunamazsa ortak not found hatası üretir.
	private Prescription getPrescription(UUID id) {
		return prescriptionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Prescription not found: " + id));
	}

	// Encounter ilişkisinin gerçekten var olduğunu doğrular.
	private Encounter getEncounter(UUID id) {
		return encounterRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Encounter not found: " + id));
	}

	// Patient ilişkisinin gerçekten var olduğunu doğrular.
	private Patient getPatient(UUID id) {
		return patientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
	}

	// Doctor ilişkisinin gerçekten var olduğunu doğrular.
	private Doctor getDoctor(UUID id) {
		return doctorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
	}

	// Reçete oluşturulurken encounter ile patient/doctor ilişkisi uyumlu olmalıdır.
	// Bu kontrol klinik tutarlılığı korur ve yanlış kişiye veya yanlış doktora bağlı reçete oluşmasını engeller.
	private void validatePrescriptionRelations(Encounter encounter, Patient patient, Doctor doctor) {
		if (!encounter.getPatient().getId().equals(patient.getId())) {
			throw new BusinessRuleViolationException("Prescription patient must match encounter patient");
		}
		if (!encounter.getDoctor().getId().equals(doctor.getId())) {
			throw new BusinessRuleViolationException("Prescription doctor must match encounter doctor");
		}
	}
}
