package com.hospital.inventory.stock.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospital.inventory.stock.model.StockBatch;

public interface StockBatchRepository extends JpaRepository<StockBatch, UUID> {

	List<StockBatch> findAllByInventoryItemIdAndActiveTrueOrderByExpiresAtAscBatchNumberAsc(UUID inventoryItemId);

	@Query("""
			select coalesce(sum(batch.quantityOnHand), 0)
			from StockBatch batch
			where batch.inventoryItem.id = :itemId
			  and batch.active = true
			""")
	BigDecimal sumQuantityOnHandByItemId(@Param("itemId") UUID itemId);
}
