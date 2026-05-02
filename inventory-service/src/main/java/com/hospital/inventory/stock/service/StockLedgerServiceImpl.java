package com.hospital.inventory.stock.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.StockAvailabilityBatchResponse;
import com.hospital.inventory.stock.dto.StockAvailabilityResponse;
import com.hospital.inventory.stock.dto.StockMovementResponse;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockMovement;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;

@Service
public class StockLedgerServiceImpl implements StockLedgerService {

	private final InventoryItemRepository inventoryItemRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockMovementRepository stockMovementRepository;

	public StockLedgerServiceImpl(
			InventoryItemRepository inventoryItemRepository,
			StockBatchRepository stockBatchRepository,
			StockMovementRepository stockMovementRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockMovementRepository = stockMovementRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public StockAvailabilityResponse getAvailability(UUID itemId) {
		ensureItemExists(itemId);

		BigDecimal totalOnHand = stockBatchRepository.sumQuantityOnHandByItemId(itemId);
		StockAvailabilityResponse response = new StockAvailabilityResponse();
		response.setItemId(itemId);
		response.setTotalOnHand(totalOnHand);
		response.setReservedQuantity(BigDecimal.ZERO);
		response.setAvailableQuantity(totalOnHand);
		response.setBatches(stockBatchRepository.findAllByInventoryItemIdAndActiveTrueOrderByExpiresAtAscBatchNumberAsc(itemId)
				.stream()
				.map(this::toBatchResponse)
				.toList());
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<StockMovementResponse> getMovements(UUID itemId, Pageable pageable) {
		ensureItemExists(itemId);
		return stockMovementRepository.findAllByInventoryItemIdOrderByOccurredAtDesc(itemId, pageable)
				.map(this::toMovementResponse);
	}

	private void ensureItemExists(UUID itemId) {
		if (!inventoryItemRepository.existsById(itemId)) {
			throw new ResourceNotFoundException("Inventory item not found: " + itemId);
		}
	}

	private StockAvailabilityBatchResponse toBatchResponse(StockBatch batch) {
		StockAvailabilityBatchResponse response = new StockAvailabilityBatchResponse();
		response.setBatchId(batch.getId());
		response.setWarehouseId(batch.getWarehouse().getId());
		response.setWarehouseZoneId(batch.getWarehouseZone() != null ? batch.getWarehouseZone().getId() : null);
		response.setBatchNumber(batch.getBatchNumber());
		response.setExpiresAt(batch.getExpiresAt());
		response.setQuantityOnHand(batch.getQuantityOnHand());
		return response;
	}

	private StockMovementResponse toMovementResponse(StockMovement movement) {
		StockMovementResponse response = new StockMovementResponse();
		response.setId(movement.getId());
		response.setItemId(movement.getInventoryItem().getId());
		response.setBatchId(movement.getStockBatch() != null ? movement.getStockBatch().getId() : null);
		response.setWarehouseId(movement.getWarehouse().getId());
		response.setWarehouseZoneId(movement.getWarehouseZone() != null ? movement.getWarehouseZone().getId() : null);
		response.setMovementType(movement.getMovementType().name());
		response.setQuantity(movement.getQuantity());
		response.setOccurredAt(movement.getOccurredAt());
		response.setReferenceType(movement.getReferenceType());
		response.setReferenceId(movement.getReferenceId());
		response.setNotes(movement.getNotes());
		return response;
	}
}
