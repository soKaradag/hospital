package com.hospital.inventory.stock.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.stock.model.StockCount;

public interface StockCountRepository extends JpaRepository<StockCount, UUID> {

	@EntityGraph(attributePaths = { "warehouse", "warehouseZone" })
	Optional<StockCount> findById(UUID id);
}
