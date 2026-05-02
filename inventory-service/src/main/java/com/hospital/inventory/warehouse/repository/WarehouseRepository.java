package com.hospital.inventory.warehouse.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.warehouse.model.Warehouse;

public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

	boolean existsByCodeIgnoreCase(String code);

	Optional<Warehouse> findByCodeIgnoreCase(String code);

	Page<Warehouse> findAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
			String name,
			String code,
			Pageable pageable);
}
