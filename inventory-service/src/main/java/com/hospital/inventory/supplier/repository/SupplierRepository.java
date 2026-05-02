package com.hospital.inventory.supplier.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.supplier.model.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

	boolean existsByCodeIgnoreCase(String code);

	Optional<Supplier> findByCodeIgnoreCase(String code);

	Page<Supplier> findAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
			String name,
			String code,
			Pageable pageable);
}
