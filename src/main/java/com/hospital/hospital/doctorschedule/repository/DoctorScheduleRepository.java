package com.hospital.hospital.doctorschedule.repository;

import java.time.DayOfWeek;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.doctorschedule.model.DoctorSchedule;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {

	Page<DoctorSchedule> findAllByDoctorId(UUID doctorId, Pageable pageable);

	Page<DoctorSchedule> findAllByDayOfWeek(DayOfWeek dayOfWeek, Pageable pageable);
}
