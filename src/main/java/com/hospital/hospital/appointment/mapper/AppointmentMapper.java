package com.hospital.hospital.appointment.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.appointment.dto.AppointmentResponse;
import com.hospital.hospital.appointment.dto.CreateAppointmentRequest;
import com.hospital.hospital.appointment.dto.UpdateAppointmentRequest;
import com.hospital.hospital.appointment.model.Appointment;

// Appointment entity ve dto dönüşümlerini manuel olarak yönetir.
// Kullanım amacı DTO'lar üzerinden entity'ye veri aktarmak ve entity'den DTO'ya veri aktarmaktır.
// Mapper olmadığı takdirde service katmanında entity ve dto dönüşümleri yapılır.
// MapStruct kütüphanesi ile otomatik mapper oluşturulabilir.
// Manuel yazmanın amacı mapperın nasıl çalıştığını anlamaktır.
@Component
public class AppointmentMapper {

	// CreateAppointmentRequest'i Appointment entity'ye dönüştürür.
	public Appointment toEntity(CreateAppointmentRequest request) {
		if (request == null) {
			return null;
		}

		// Burada yeni bir Appointment nesnesi oluşturmak memory leak'e yol açmaz.
		// Çünkü bu nesne metodun normal akışı içinde oluşturulur ve çağıran katmana döndürülür.
		// Java'da referansı yönetilemeyen nesneler Garbage Collector tarafından temizlenir.
		//
		// Memory leak genelde şu durumlarda oluşur:
		// - Nesneler static alanlarda gereksiz yere tutulursa
		// - Sürekli büyüyen liste, map veya cache içinde biriktirilirse
		// - Listener, thread veya oturum benzeri uzun ömürlü yapılarda referans bırakılırsa
		// - Kullanılmayan nesnelerin referansı elde tutulmaya devam edilirse
		//
		// Bu metotta ise nesne yalnızca dönüşüm amacıyla oluşturulur.
		// Yani yaşam döngüsü kontrollüdür ve normal object creation senaryosudur.
		Appointment appointment = new Appointment();
		appointment.setAppointmentDateTime(request.getAppointmentDateTime());
		appointment.setStatus(request.getStatus());
		appointment.setNotes(request.getNotes());
		return appointment;
	}

	// UpdateAppointmentRequest'i Appointment entity'ye dönüştürür.
	public void updateEntity(UpdateAppointmentRequest request, Appointment appointment) {
		if (request == null || appointment == null) {
			return;
		}
		appointment.setAppointmentDateTime(request.getAppointmentDateTime());
		appointment.setStatus(request.getStatus());
		appointment.setNotes(request.getNotes());
	}

	// Appointment entity'yi AppointmentResponse dto'ya dönüştürür.
	public AppointmentResponse toResponse(Appointment appointment) {
		return toResponse(appointment, 0L);
	}

	public AppointmentResponse toResponse(Appointment appointment, long statusHistoryCount) {
		if (appointment == null) {
			return null;
		}
		AppointmentResponse response = new AppointmentResponse();
		response.setId(appointment.getId());
		if (appointment.getPatient() != null) {
			response.setPatientId(appointment.getPatient().getId());
			response.setPatientFullName(appointment.getPatient().getFirstName() + " " + appointment.getPatient().getLastName());
		}
		if (appointment.getDoctor() != null) {
			response.setDoctorId(appointment.getDoctor().getId());
			response.setDoctorFullName(appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());
		}
		response.setAppointmentDateTime(appointment.getAppointmentDateTime());
		response.setStatus(appointment.getStatus());
		response.setNotes(appointment.getNotes());
		response.setStatusHistoryCount(statusHistoryCount);
		response.setCreatedAt(appointment.getCreatedAt());
		response.setUpdatedAt(appointment.getUpdatedAt());
		return response;
	}
}
