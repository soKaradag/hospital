package com.hospital.hospital.disease.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.disease.model.Disease;

public interface DiseaseRepository extends JpaRepository<Disease, UUID> {

	Optional<Disease> findByCode(String code);

	boolean existsByCode(String code);

	Optional<Disease> findByIdAndActiveTrue(UUID id);

	Page<Disease> findAllByActiveTrue(Pageable pageable);

	Page<Disease> findAllByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
			String codeKeyword,
			String nameKeyword,
			Pageable pageable);

	Page<Disease> findAllByActiveTrueAndCodeContainingIgnoreCaseOrActiveTrueAndNameContainingIgnoreCase(
			String codeKeyword,
			String nameKeyword,
			Pageable pageable);
}
