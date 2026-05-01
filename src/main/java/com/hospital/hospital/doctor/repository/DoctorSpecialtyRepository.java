package com.hospital.hospital.doctor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.doctor.model.DoctorSpecialty;

public interface DoctorSpecialtyRepository extends JpaRepository<DoctorSpecialty, UUID> {

	Optional<DoctorSpecialty> findByDoctorIdAndPrimaryTrue(UUID doctorId);

	void deleteAllByDoctorId(UUID doctorId);
}
