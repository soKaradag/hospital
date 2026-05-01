package com.hospital.hospital.doctorschedule.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.doctorschedule.model.DoctorScheduleException;

public interface DoctorScheduleExceptionRepository extends JpaRepository<DoctorScheduleException, UUID> {

	long countByDoctorScheduleId(UUID doctorScheduleId);
}
