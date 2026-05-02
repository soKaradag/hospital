package com.hospital.inventory.inventorycategory.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.inventorycategory.model.InventoryCategory;

public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, UUID> {

	boolean existsByCodeIgnoreCase(String code);

	Optional<InventoryCategory> findByCodeIgnoreCase(String code);

	Page<InventoryCategory> findAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
			String name,
			String code,
			Pageable pageable);
}
