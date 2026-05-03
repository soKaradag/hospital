package com.hospital.inventory.stock.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockReservationRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;

@Service
public class StockBatchAllocationService {

	private final StockBatchRepository stockBatchRepository;
	private final StockReservationRepository stockReservationRepository;

	public StockBatchAllocationService(
			StockBatchRepository stockBatchRepository,
			StockReservationRepository stockReservationRepository) {
		this.stockBatchRepository = stockBatchRepository;
		this.stockReservationRepository = stockReservationRepository;
	}

	public BatchAllocation resolveExplicitBatchAllocation(
			InventoryItem item,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			UUID batchId,
			BigDecimal requestedQuantity) {
		StockBatch batch = getBatchForLocation(item, warehouse, warehouseZone, batchId);
		BigDecimal availableQuantity = getAvailableQuantity(batch, BigDecimal.ZERO);
		if (availableQuantity.compareTo(requestedQuantity) < 0) {
			throw new BusinessRuleViolationException("Insufficient stock available in the requested batch");
		}
		return new BatchAllocation(batch, requestedQuantity);
	}

	public BatchAllocation resolveSingleBatchAllocation(
			InventoryItem item,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			BigDecimal requestedQuantity) {
		for (StockBatch batch : getCandidateBatches(item, warehouse, warehouseZone)) {
			BigDecimal availableQuantity = getAvailableQuantity(batch, BigDecimal.ZERO);
			if (availableQuantity.compareTo(requestedQuantity) >= 0) {
				return new BatchAllocation(batch, requestedQuantity);
			}
		}
		throw new BusinessRuleViolationException("A single batch with sufficient stock is required for reservation");
	}

	public List<BatchAllocation> allocateAcrossBatches(
			InventoryItem item,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			BigDecimal requestedQuantity,
			Map<UUID, BigDecimal> pendingReservedQuantities) {
		BigDecimal remaining = requestedQuantity;
		List<BatchAllocation> allocations = new ArrayList<>();
		for (StockBatch batch : getCandidateBatches(item, warehouse, warehouseZone)) {
			BigDecimal pendingQuantity = pendingReservedQuantities.getOrDefault(batch.getId(), BigDecimal.ZERO);
			BigDecimal availableQuantity = getAvailableQuantity(batch, pendingQuantity);
			if (availableQuantity.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			BigDecimal allocatedQuantity = availableQuantity.min(remaining);
			if (allocatedQuantity.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			allocations.add(new BatchAllocation(batch, allocatedQuantity));
			pendingReservedQuantities.merge(batch.getId(), allocatedQuantity, BigDecimal::add);
			remaining = remaining.subtract(allocatedQuantity);
			if (remaining.compareTo(BigDecimal.ZERO) == 0) {
				return allocations;
			}
		}
		throw new BusinessRuleViolationException("Insufficient stock available for reservation");
	}

	public StockBatch getBatchForLocation(
			InventoryItem item,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			UUID batchId) {
		StockBatch batch = stockBatchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Stock batch not found: " + batchId));
		if (!batch.getInventoryItem().getId().equals(item.getId())) {
			throw new BusinessRuleViolationException("Batch does not belong to the requested inventory item");
		}
		if (!batch.getWarehouse().getId().equals(warehouse.getId())) {
			throw new BusinessRuleViolationException("Stock batch does not belong to the requested warehouse");
		}
		if (!sameZone(batch.getWarehouseZone(), warehouseZone)) {
			throw new BusinessRuleViolationException("Stock batch does not belong to the requested warehouse zone");
		}
		return batch;
	}

	private List<StockBatch> getCandidateBatches(InventoryItem item, Warehouse warehouse, WarehouseZone warehouseZone) {
		return stockBatchRepository.findAllByItemAndLocationOrderByExpiry(
				item.getId(),
				warehouse.getId(),
				warehouseZone != null ? warehouseZone.getId() : null);
	}

	private BigDecimal getAvailableQuantity(StockBatch batch, BigDecimal pendingReservedQuantity) {
		BigDecimal reservedQuantity = stockReservationRepository.sumQuantityByBatchIdAndStatus(
				batch.getId(),
				ReservationStatus.ACTIVE);
		return batch.getQuantityOnHand().subtract(reservedQuantity).subtract(pendingReservedQuantity);
	}

	private boolean sameZone(WarehouseZone left, WarehouseZone right) {
		if (left == null || right == null) {
			return left == right;
		}
		return left.getId().equals(right.getId());
	}

	public record BatchAllocation(StockBatch batch, BigDecimal quantity) {
	}
}
