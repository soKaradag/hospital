package com.hospital.hospital.doctorschedule.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.doctorschedule.model.DoctorLeave;

public interface DoctorLeaveRepository extends JpaRepository<DoctorLeave, UUID> {

	long countByDoctorId(UUID doctorId);
}
