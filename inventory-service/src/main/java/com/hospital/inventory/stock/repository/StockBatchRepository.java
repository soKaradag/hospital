package com.hospital.inventory.stock.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hospital.inventory.stock.model.StockBatch;

public interface StockBatchRepository extends JpaRepository<StockBatch, UUID> {

	@Query("""
			select batch
			from StockBatch batch
			where batch.inventoryItem.id = :itemId
			  and batch.warehouse.id = :warehouseId
			  and ((:warehouseZoneId is null and batch.warehouseZone is null)
			    or batch.warehouseZone.id = :warehouseZoneId)
			  and lower(batch.batchNumber) = lower(:batchNumber)
			  and ((:expiresAt is null and batch.expiresAt is null)
			    or batch.expiresAt = :expiresAt)
			""")
	Optional<StockBatch> findMatchingBatch(
			@Param("itemId") UUID itemId,
			@Param("warehouseId") UUID warehouseId,
			@Param("warehouseZoneId") UUID warehouseZoneId,
			@Param("batchNumber") String batchNumber,
			@Param("expiresAt") LocalDate expiresAt);

	@Query("""
			select batch
			from StockBatch batch
			where batch.inventoryItem.id = :itemId
			  and batch.warehouse.id = :warehouseId
			  and ((:warehouseZoneId is null and batch.warehouseZone is null)
			    or batch.warehouseZone.id = :warehouseZoneId)
			  and batch.active = true
			order by
			  case when batch.expiresAt is null then 1 else 0 end,
			  batch.expiresAt asc,
			  batch.batchNumber asc
			""")
	List<StockBatch> findAllByItemAndLocationOrderByExpiry(
			@Param("itemId") UUID itemId,
			@Param("warehouseId") UUID warehouseId,
			@Param("warehouseZoneId") UUID warehouseZoneId);

	List<StockBatch> findAllByInventoryItemIdAndActiveTrueOrderByExpiresAtAscBatchNumberAsc(UUID inventoryItemId);

	@Query("""
			select coalesce(sum(batch.quantityOnHand), 0)
			from StockBatch batch
			where batch.inventoryItem.id = :itemId
			  and batch.active = true
			""")
	BigDecimal sumQuantityOnHandByItemId(@Param("itemId") UUID itemId);

	@Query("""
			select coalesce(sum(batch.quantityOnHand), 0)
			from StockBatch batch
			where batch.inventoryItem.id = :itemId
			  and batch.warehouse.id = :warehouseId
			  and ((:warehouseZoneId is null and batch.warehouseZone is null)
			    or batch.warehouseZone.id = :warehouseZoneId)
			  and batch.active = true
			""")
	BigDecimal sumQuantityOnHandByLocation(
			@Param("itemId") UUID itemId,
			@Param("warehouseId") UUID warehouseId,
			@Param("warehouseZoneId") UUID warehouseZoneId);
}
