package com.hospital.hospital.doctorschedule.service;

import java.time.DayOfWeek;
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
import com.hospital.hospital.doctorschedule.dto.CreateDoctorScheduleRequest;
import com.hospital.hospital.doctorschedule.dto.DoctorScheduleResponse;
import com.hospital.hospital.doctorschedule.dto.UpdateDoctorScheduleRequest;
import com.hospital.hospital.doctorschedule.mapper.DoctorScheduleMapper;
import com.hospital.hospital.doctorschedule.model.DoctorSchedule;
import com.hospital.hospital.doctorschedule.repository.DoctorLeaveRepository;
import com.hospital.hospital.doctorschedule.repository.DoctorScheduleExceptionRepository;
import com.hospital.hospital.doctorschedule.repository.DoctorScheduleRepository;

/*
- Bu sınıf doctor schedule domain'inin iş kurallarını uygular.
- Controller'dan gelen istek burada doğrulanır, gerekli ilişki kontrolleri yapılır ve repository katmanına yönlendirilir.
- Mapper kullanımı sayesinde entity ile dış API modeli birbirine karışmaz.
- Audit anotasyonları, schedule oluşturma ve güncelleme işlemlerinin ayrıca loglanmasını sağlar.
*/
@Service
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

	private final DoctorScheduleRepository doctorScheduleRepository;
	private final DoctorRepository doctorRepository;
	private final DoctorScheduleMapper doctorScheduleMapper;
	private final DoctorLeaveRepository doctorLeaveRepository;
	private final DoctorScheduleExceptionRepository doctorScheduleExceptionRepository;

	public DoctorScheduleServiceImpl(
			DoctorScheduleRepository doctorScheduleRepository,
			DoctorRepository doctorRepository,
			DoctorScheduleMapper doctorScheduleMapper,
			DoctorLeaveRepository doctorLeaveRepository,
			DoctorScheduleExceptionRepository doctorScheduleExceptionRepository) {
		this.doctorScheduleRepository = doctorScheduleRepository;
		this.doctorRepository = doctorRepository;
		this.doctorScheduleMapper = doctorScheduleMapper;
		this.doctorLeaveRepository = doctorLeaveRepository;
		this.doctorScheduleExceptionRepository = doctorScheduleExceptionRepository;
	}

	@Override
	@Transactional
	// Yeni çalışma planı oluştururken saat aralığı doğrulanır ve doctor ilişkisi güvenli şekilde çözülür.
	@Audit(action = "CREATE_DOCTOR_SCHEDULE", entity = "DOCTOR_SCHEDULE", description = "Doctor weekly schedule creation")
	public DoctorScheduleResponse create(CreateDoctorScheduleRequest request) {
		validateTimeRange(request.getStartTime(), request.getEndTime());
		DoctorSchedule schedule = doctorScheduleMapper.toEntity(request);
		schedule.setDoctor(getDoctor(request.getDoctorId()));
		return toResponse(doctorScheduleRepository.save(schedule));
	}

	@Override
	@Transactional
	// Güncelleme akışında önce mevcut kayıt bulunur, sonra yeni alanlar entity'ye uygulanır.
	@Audit(action = "UPDATE_DOCTOR_SCHEDULE", entity = "DOCTOR_SCHEDULE", description = "Doctor weekly schedule update")
	public DoctorScheduleResponse update(UUID id, UpdateDoctorScheduleRequest request) {
		validateTimeRange(request.getStartTime(), request.getEndTime());
		DoctorSchedule schedule = getSchedule(id);
		doctorScheduleMapper.updateEntity(request, schedule);
		schedule.setDoctor(getDoctor(request.getDoctorId()));
		return toResponse(doctorScheduleRepository.save(schedule));
	}

	@Override
	@Transactional(readOnly = true)
	// Tekil okuma akışı doğrudan schedule kaydını bulup response'a dönüştürür.
	public DoctorScheduleResponse getById(UUID id) {
		return toResponse(getSchedule(id));
	}

	@Override
	@Transactional(readOnly = true)
	// Tüm schedule kayıtları sayfalı şekilde listelenir.
	public Page<DoctorScheduleResponse> getAll(Pageable pageable) {
		return doctorScheduleRepository.findAllByActiveTrue(pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Doktora göre filtrelemede önce doktorun varlığı doğrulanır; böylece sessizce boş liste dönmek yerine hatalı id erken yakalanır.
	public Page<DoctorScheduleResponse> getAllByDoctor(UUID doctorId, Pageable pageable) {
		getDoctor(doctorId);
		return doctorScheduleRepository.findAllByDoctorIdAndActiveTrue(doctorId, pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	// Haftanın gününe göre filtreleme randevu planlama tarafında temel bir sorgu zemini sağlar.
	public Page<DoctorScheduleResponse> getAllByDay(DayOfWeek dayOfWeek, Pageable pageable) {
		return doctorScheduleRepository.findAllByDayOfWeekAndActiveTrue(dayOfWeek, pageable).map(this::toResponse);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		DoctorSchedule schedule = getSchedule(id);
		schedule.setActive(false);
		doctorScheduleRepository.save(schedule);
	}

	// Kayıt bulunamadığında ortak not found hatası üretmek için tek noktadan schedule getirir.
	private DoctorSchedule getSchedule(UUID id) {
		return doctorScheduleRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor schedule not found: " + id));
	}

	// Schedule kaydı oluşturma veya filtreleme öncesi doktor ilişkisinin gerçekten var olduğunu doğrular.
	private Doctor getDoctor(UUID id) {
		return doctorRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
	}

	// Başlangıç saati bitiş saatinden önce olmalıdır; aksi halde haftalık plan anlamsız hale gelir.
	// Bu kontrol service katmanında tutulur çünkü bu bir iş kuralıdır, sadece format doğrulaması değildir.
	private void validateTimeRange(java.time.LocalTime startTime, java.time.LocalTime endTime) {
		if (startTime == null || endTime == null) {
			return;
		}
		if (!startTime.isBefore(endTime)) {
			throw new BusinessRuleViolationException("Schedule startTime must be before endTime");
		}
	}

	private DoctorScheduleResponse toResponse(DoctorSchedule schedule) {
		long leaveCount = doctorLeaveRepository.countByDoctorId(schedule.getDoctor().getId());
		long exceptionCount = doctorScheduleExceptionRepository.countByDoctorScheduleId(schedule.getId());
		return doctorScheduleMapper.toResponse(schedule, leaveCount, exceptionCount);
	}
}
