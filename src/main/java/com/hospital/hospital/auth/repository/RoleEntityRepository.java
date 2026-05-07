package com.hospital.hospital.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.auth.model.RoleEntity;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID> {

	Optional<RoleEntity> findByCode(String code);

	boolean existsByCode(String code);

	boolean existsByCodeAndIdNot(String code, UUID id);

	Page<RoleEntity> findAllByOrderByCodeAsc(Pageable pageable);
}
