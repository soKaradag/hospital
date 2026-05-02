package com.hospital.hospital.doctor.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.doctor.model.Specialty;

public interface SpecialtyRepository extends JpaRepository<Specialty, UUID> {

	Optional<Specialty> findByNameIgnoreCase(String name);
}
