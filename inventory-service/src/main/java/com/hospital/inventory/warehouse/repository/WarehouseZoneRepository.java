package com.hospital.inventory.warehouse.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.warehouse.model.WarehouseZone;

public interface WarehouseZoneRepository extends JpaRepository<WarehouseZone, UUID> {

	long countByWarehouseId(UUID warehouseId);

	boolean existsByWarehouseIdAndCodeIgnoreCase(UUID warehouseId, String code);

	Optional<WarehouseZone> findByWarehouseIdAndCodeIgnoreCase(UUID warehouseId, String code);

	Page<WarehouseZone> findAllByWarehouseId(UUID warehouseId, Pageable pageable);
}
