package com.hospital.hospital.appointment.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.appointment.dto.AppointmentResponse;
import com.hospital.hospital.appointment.dto.CreateAppointmentRequest;
import com.hospital.hospital.appointment.dto.UpdateAppointmentRequest;
import com.hospital.hospital.appointment.mapper.AppointmentMapper;
import com.hospital.hospital.appointment.model.Appointment;
import com.hospital.hospital.appointment.model.AppointmentReminder;
import com.hospital.hospital.appointment.model.AppointmentStatusHistory;
import com.hospital.hospital.appointment.repository.AppointmentProcedureRepository;
import com.hospital.hospital.appointment.repository.AppointmentReminderRepository;
import com.hospital.hospital.appointment.repository.AppointmentRepository;
import com.hospital.hospital.appointment.repository.AppointmentStatusHistoryRepository;
import com.hospital.hospital.common.exception.BusinessRuleViolationException;
import com.hospital.hospital.common.exception.ResourceNotFoundException;
import com.hospital.hospital.doctor.model.Doctor;
import com.hospital.hospital.doctor.repository.DoctorRepository;
import com.hospital.hospital.patient.model.Patient;
import com.hospital.hospital.patient.repository.PatientRepository;

/*
Impl, "Implementation" kelimesinin kısaltmasıdır ve bir interface içinde tanımlanan
davranışların gerçek kodla hayata geçirildiği sınıfı ifade eder. Örneğin bir service
interface'i ne yapılacağını tanımlarken, ilgili Impl sınıfı bunun nasıl yapılacağını
yazar. Bu yaklaşım soyutlama ile uygulamayı ayırdığı için kodu daha düzenli, test
edilebilir ve genişletilebilir hale getirir; aynı zamanda bağımlılıkları gevşetir,
mock kullanımını kolaylaştırır ve proje büyüdükçe farklı implementasyonlar eklemeyi
mümkün kılar.
*/


// Service katmanı, iş kurallarını ve veri erişimini yönetir.
// Service anatasyonu, bu sınıfın bir Spring Bean olduğunu ve servis katmanında kullanılacağını belirtir.
// AppointmentServiceImpl sınıfı, AppointmentService interface'ini implement eder.
@Service
public class AppointmentServiceImpl implements AppointmentService {

	/*
	- Bu alanlar sınıfın ihtiyaç duyduğu bağımlılıklardır.
	- Spring bu bağımlılıkları Dependency Injection ile dışarıdan verir.
	- Yani sınıf kendi içinde new ile nesne üretmez, hazır nesneyi alır.
	- Bunun sebebi sınıfın sadece kendi iş mantığına odaklanmasını sağlamaktır.
	- Eğer burada new kullansaydık, sınıf hangi nesnenin nasıl oluşturulacağını da bilmek zorunda kalırdı.
	- Bu durum kodu daha sıkı bağlı hale getirir.
	- Sıkı bağlı yapı test yazmayı ve mock kullanmayı zorlaştırır.
	- Farklı bir implementasyona geçmek de daha zor olur.
	- Spring bean yaşam döngüsünü ve yönetimini de daha zor kontrol eder.
	- Dependency Injection ile nesne oluşturma sorumluluğu Spring container'a bırakılır.
	- final kullanımı, bu bağımlılıkların sadece bir kez atanacağını ve sonradan değişmeyeceğini garanti eder.
	- Bu kullanım memory leak oluşturmaz; çünkü burada gereksiz nesne biriktirilmez, sadece yönetilen bean referansları tutulur.
	*/
	private final AppointmentRepository appointmentRepository;
	private final AppointmentProcedureRepository appointmentProcedureRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;
	private final AppointmentMapper appointmentMapper;
	private final AppointmentStatusHistoryRepository appointmentStatusHistoryRepository;
	private final AppointmentReminderRepository appointmentReminderRepository;

	/*
	- Bu constructor, Spring tarafından çağrılır.
	- Spring, bu constructor'a gerekli nesneleri otomatik olarak verir.
	- Bu nesneler, Spring container'da zaten oluşturulmuş olan bean'lerdir.
	- Bu constructor, sınıfın ihtiyaç duyduğu bağımlılıkları alır.
	- Bu bağımlılıklar, sınıfın iş mantığını uygulamak için kullanılır.
	- Bu constructor, sınıfın ihtiyaç duyduğu bağımlılıkları alır.
	- Bu bağımlılıklar, sınıfın iş mantığını uygulamak için kullanılır.
	*/
	public AppointmentServiceImpl(AppointmentRepository appointmentRepository,
			AppointmentProcedureRepository appointmentProcedureRepository, PatientRepository patientRepository,
			DoctorRepository doctorRepository, AppointmentMapper appointmentMapper,
			AppointmentStatusHistoryRepository appointmentStatusHistoryRepository,
			AppointmentReminderRepository appointmentReminderRepository) {
		this.appointmentRepository = appointmentRepository;
		this.appointmentProcedureRepository = appointmentProcedureRepository;
		this.patientRepository = patientRepository;
		this.doctorRepository = doctorRepository;
		this.appointmentMapper = appointmentMapper;
		this.appointmentStatusHistoryRepository = appointmentStatusHistoryRepository;
		this.appointmentReminderRepository = appointmentReminderRepository;
	}

	/*
	- @Override, bu metodun interface içinde tanımlanan bir metodu uyguladığını belirtir.
	- Bu anotasyon sayesinde metod imzası yanlış yazılırsa derleme aşamasında hata alınır.
	- @Transactional ise bu metodun veritabanı işlemlerinin tek bir transaction içinde çalışacağını belirtir.
	- Yani bu metod içindeki veritabanı adımları bir bütün olarak ele alınır.
	- Eğer tüm işlemler başarılı olursa transaction commit edilir ve değişiklikler kalıcı olur.
	- Eğer işlem sırasında hata oluşursa transaction rollback edilir ve yapılan değişiklikler geri alınır.
	- Bu, veritabanı tutarlılığı için çok önemlidir.
	- Burada önce randevu zamanı kontrol edilir.
	- Sonra request verisi entity'ye çevrilir.
	- Ardından patient ve doctor ilişkileri yüklenip entity'ye bağlanır.
	- En son appointmentRepository.save(appointment) ile kayıt veritabanına yazılır.
	- Bu akış transaction içinde olduğu için işlemler yarım kalmış bozuk bir durum oluşturmaz.
	- Özellikle birden fazla veritabanı adımı içeren create, update ve delete işlemlerinde @Transactional kullanmak doğru yaklaşımdır.
	*/
	@Override
	@Transactional
	public AppointmentResponse create(CreateAppointmentRequest request) {
		validateAppointmentTime(request.getAppointmentDateTime());
		Appointment appointment = appointmentMapper.toEntity(request);
		appointment.setPatient(getPatient(request.getPatientId()));
		appointment.setDoctor(getDoctor(request.getDoctorId()));
		Appointment savedAppointment = appointmentRepository.save(appointment);
		appendStatusHistory(savedAppointment);
		syncPreVisitReminder(savedAppointment);
		return toResponse(savedAppointment);
	}

	@Override
	@Transactional
	public AppointmentResponse createWithProcedure(CreateAppointmentRequest request) {
		validateAppointmentTime(request.getAppointmentDateTime());

		// Procedure çağrısından önce foreign key tarafındaki varlıklar uygulama katmanında doğrulanır.
		// Böylece veritabanı tarafına daha kontrollü ve anlamlı veri gönderilir.
		getPatient(request.getPatientId());
		getDoctor(request.getDoctorId());

		UUID appointmentId = UUID.randomUUID();

		try {
			boolean conflictFound = appointmentProcedureRepository.createAppointmentIfAvailable(appointmentId, request);
			if (conflictFound) {
				throw new BusinessRuleViolationException(
						"Doctor already has an appointment at the selected date and time");
			}
		} catch (DataAccessException exception) {
			throw new BusinessRuleViolationException(
					"Appointment procedure is not available. Run phase-3 advanced SQL scripts first.");
		}

		Appointment appointment = getAppointment(appointmentId);
		appendStatusHistory(appointment);
		syncPreVisitReminder(appointment);
		return toResponse(appointment);
	}

	@Override
	@Transactional
	public AppointmentResponse update(UUID id, UpdateAppointmentRequest request) {
		validateAppointmentTime(request.getAppointmentDateTime());
		Appointment appointment = getAppointment(id);
		appointmentMapper.updateEntity(request, appointment);
		appointment.setPatient(getPatient(request.getPatientId()));
		appointment.setDoctor(getDoctor(request.getDoctorId()));
		Appointment savedAppointment = appointmentRepository.save(appointment);
		appendStatusHistory(savedAppointment);
		syncPreVisitReminder(savedAppointment);
		return toResponse(savedAppointment);
	}

	@Override
	@Transactional(readOnly = true)
	public AppointmentResponse getById(UUID id) {
		return toResponse(getAppointment(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AppointmentResponse> getAll(Pageable pageable) {
		return appointmentRepository.findAll(pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AppointmentResponse> getAllByPatient(UUID patientId, Pageable pageable) {
		getPatient(patientId);
		return appointmentRepository.findAllByPatientId(patientId, pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AppointmentResponse> getAllByDoctor(UUID doctorId, Pageable pageable) {
		getDoctor(doctorId);
		return appointmentRepository.findAllByDoctorId(doctorId, pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AppointmentResponse> getAllByDateRange(Instant startInclusive, Instant endInclusive, Pageable pageable) {
		return appointmentRepository.findAllByAppointmentDateTimeBetween(startInclusive, endInclusive, pageable)
				.map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AppointmentResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		return appointmentRepository.findAllByNotesContainingIgnoreCase(keyword.trim(), pageable)
				.map(this::toResponse);
	}

	private Appointment getAppointment(UUID id) {
		return appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + id));
	}

	private Patient getPatient(UUID id) {
		return patientRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found: " + id));
	}

	private Doctor getDoctor(UUID id) {
		return doctorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found: " + id));
	}

	private void validateAppointmentTime(Instant appointmentDateTime) {
		if (appointmentDateTime != null && appointmentDateTime.isBefore(Instant.now())) {
			throw new BusinessRuleViolationException("Appointment dateTime cannot be in the past");
		}
	}

	private void appendStatusHistory(Appointment appointment) {
		AppointmentStatusHistory history = new AppointmentStatusHistory();
		history.setAppointment(appointment);
		history.setStatus(appointment.getStatus());
		history.setNotes(appointment.getNotes());
		history.setChangedAt(Instant.now());
		appointmentStatusHistoryRepository.save(history);
	}

	private AppointmentResponse toResponse(Appointment appointment) {
		long statusHistoryCount = appointmentStatusHistoryRepository.countByAppointmentId(appointment.getId());
		long reminderCount = appointmentReminderRepository.countByAppointmentId(appointment.getId());
		return appointmentMapper.toResponse(appointment, statusHistoryCount, reminderCount);
	}

	private void syncPreVisitReminder(Appointment appointment) {
		if (appointment.getAppointmentDateTime() == null || !appointment.getAppointmentDateTime().isAfter(Instant.now().plusSeconds(3600))) {
			return;
		}
		AppointmentReminder reminder = appointmentReminderRepository
				.findByAppointmentIdAndReminderType(appointment.getId(), "PRE_VISIT")
				.orElseGet(AppointmentReminder::new);
		reminder.setAppointment(appointment);
		reminder.setReminderType("PRE_VISIT");
		reminder.setScheduledAt(appointment.getAppointmentDateTime().minusSeconds(24 * 60 * 60));
		reminder.setStatus("SCHEDULED");
		reminder.setChannel("SMS");
		reminder.setMessage("Reminder for appointment " + appointment.getId());
		appointmentReminderRepository.save(reminder);
	}
}
