package com.hospital.hospital.disease.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.disease.model.DiseaseCategory;

public interface DiseaseCategoryRepository extends JpaRepository<DiseaseCategory, UUID> {

	Optional<DiseaseCategory> findByCode(String code);
}
