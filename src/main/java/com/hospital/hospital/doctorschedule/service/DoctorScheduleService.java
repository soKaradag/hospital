package com.hospital.hospital.doctorschedule.service;

import java.time.DayOfWeek;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.doctorschedule.dto.CreateDoctorScheduleRequest;
import com.hospital.hospital.doctorschedule.dto.DoctorScheduleResponse;
import com.hospital.hospital.doctorschedule.dto.UpdateDoctorScheduleRequest;

/*
- Bu servis sözleşmesi doktor çalışma planı akışlarını tanımlar.
- Controller yalnızca bu arayüzü bilir; veri erişim ve iş kuralı detayları implementation içinde kalır.
- Böylece controller ile iş mantığı ayrışır ve ileride farklı implementasyonlara alan açılır.
*/
public interface DoctorScheduleService {

	// Yeni doktor çalışma planı oluşturur.
	DoctorScheduleResponse create(CreateDoctorScheduleRequest request);

	// Mevcut doktor çalışma planını günceller.
	DoctorScheduleResponse update(UUID id, UpdateDoctorScheduleRequest request);

	// Tek bir çalışma planını kimliğine göre getirir.
	DoctorScheduleResponse getById(UUID id);

	// Tüm çalışma planlarını sayfalı şekilde listeler.
	Page<DoctorScheduleResponse> getAll(Pageable pageable);

	// Belirli bir doktora ait çalışma planlarını sayfalı şekilde getirir.
	Page<DoctorScheduleResponse> getAllByDoctor(UUID doctorId, Pageable pageable);

	// Haftanın belirli bir gününe ait çalışma planlarını sayfalı şekilde getirir.
	Page<DoctorScheduleResponse> getAllByDay(DayOfWeek dayOfWeek, Pageable pageable);
}
