package com.hospital.inventory.stock.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.inventory.stock.model.StockMovement;

public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

	Page<StockMovement> findAllByInventoryItemIdOrderByOccurredAtDesc(UUID inventoryItemId, Pageable pageable);
}
