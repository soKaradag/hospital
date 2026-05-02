package com.hospital.hospital.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.hospital.auth.model.RoleEntity;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID> {

	Optional<RoleEntity> findByCode(String code);
}
