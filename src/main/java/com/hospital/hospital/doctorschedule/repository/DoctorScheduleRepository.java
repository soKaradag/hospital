package com.hospital.hospital.doctorschedule.repository;

import java.time.DayOfWeek;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.doctorschedule.model.DoctorSchedule;

public interface DoctorScheduleRepository extends JpaRepository<DoctorSchedule, UUID> {

	java.util.Optional<DoctorSchedule> findByIdAndActiveTrue(UUID id);

	Page<DoctorSchedule> findAllByActiveTrue(Pageable pageable);

	Page<DoctorSchedule> findAllByDoctorIdAndActiveTrue(UUID doctorId, Pageable pageable);

	Page<DoctorSchedule> findAllByDayOfWeekAndActiveTrue(DayOfWeek dayOfWeek, Pageable pageable);
}
