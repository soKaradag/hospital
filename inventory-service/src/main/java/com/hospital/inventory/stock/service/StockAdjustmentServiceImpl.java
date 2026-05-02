package com.hospital.inventory.stock.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockAdjustmentRequest;
import com.hospital.inventory.stock.dto.StockAdjustmentResponse;
import com.hospital.inventory.stock.model.MovementType;
import com.hospital.inventory.stock.model.StockAdjustment;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockMovement;
import com.hospital.inventory.stock.repository.StockAdjustmentRepository;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@Service
public class StockAdjustmentServiceImpl implements StockAdjustmentService {

	private final InventoryItemRepository inventoryItemRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockMovementRepository stockMovementRepository;
	private final StockAdjustmentRepository stockAdjustmentRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;

	public StockAdjustmentServiceImpl(
			InventoryItemRepository inventoryItemRepository,
			StockBatchRepository stockBatchRepository,
			StockMovementRepository stockMovementRepository,
			StockAdjustmentRepository stockAdjustmentRepository,
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockMovementRepository = stockMovementRepository;
		this.stockAdjustmentRepository = stockAdjustmentRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
	}

	@Override
	@Transactional
	public StockAdjustmentResponse create(CreateStockAdjustmentRequest request) {
		InventoryItem item = inventoryItemRepository.findById(request.getItemId())
				.orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + request.getItemId()));
		StockBatch batch = stockBatchRepository.findById(request.getBatchId())
				.orElseThrow(() -> new ResourceNotFoundException("Stock batch not found: " + request.getBatchId()));
		Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + request.getWarehouseId()));
		WarehouseZone zone = request.getWarehouseZoneId() != null
				? warehouseZoneRepository.findById(request.getWarehouseZoneId())
						.orElseThrow(() -> new ResourceNotFoundException(
								"Warehouse zone not found: " + request.getWarehouseZoneId()))
				: null;

		if (!batch.getInventoryItem().getId().equals(item.getId())) {
			throw new BusinessRuleViolationException("Batch does not belong to the requested inventory item");
		}

		BigDecimal quantityDelta = request.getQuantityDelta();
		if (quantityDelta.compareTo(BigDecimal.ZERO) == 0) {
			throw new BusinessRuleViolationException("quantityDelta must not be zero");
		}

		BigDecimal updatedQuantity = batch.getQuantityOnHand().add(quantityDelta);
		if (updatedQuantity.compareTo(BigDecimal.ZERO) < 0) {
			throw new BusinessRuleViolationException("Adjustment would make batch quantity negative");
		}

		batch.setQuantityOnHand(updatedQuantity);
		stockBatchRepository.save(batch);

		StockAdjustment adjustment = new StockAdjustment();
		adjustment.setInventoryItem(item);
		adjustment.setStockBatch(batch);
		adjustment.setWarehouse(warehouse);
		adjustment.setWarehouseZone(zone);
		adjustment.setQuantityDelta(quantityDelta);
		adjustment.setReasonCode(request.getReasonCode().trim());
		adjustment.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);

		StockAdjustment savedAdjustment = stockAdjustmentRepository.save(adjustment);
		stockMovementRepository.save(createMovement(savedAdjustment));
		return toResponse(savedAdjustment);
	}

	private StockMovement createMovement(StockAdjustment adjustment) {
		StockMovement movement = new StockMovement();
		movement.setInventoryItem(adjustment.getInventoryItem());
		movement.setStockBatch(adjustment.getStockBatch());
		movement.setWarehouse(adjustment.getWarehouse());
		movement.setWarehouseZone(adjustment.getWarehouseZone());
		movement.setMovementType(MovementType.ADJUSTMENT);
		movement.setQuantity(adjustment.getQuantityDelta());
		movement.setOccurredAt(Instant.now());
		movement.setReferenceType(adjustment.getReasonCode());
		movement.setReferenceId(adjustment.getId().toString());
		movement.setNotes(adjustment.getNotes());
		return movement;
	}

	private StockAdjustmentResponse toResponse(StockAdjustment adjustment) {
		StockAdjustmentResponse response = new StockAdjustmentResponse();
		response.setId(adjustment.getId());
		response.setItemId(adjustment.getInventoryItem().getId());
		response.setBatchId(adjustment.getStockBatch().getId());
		response.setWarehouseId(adjustment.getWarehouse().getId());
		response.setWarehouseZoneId(adjustment.getWarehouseZone() != null ? adjustment.getWarehouseZone().getId() : null);
		response.setQuantityDelta(adjustment.getQuantityDelta());
		response.setReasonCode(adjustment.getReasonCode());
		response.setNotes(adjustment.getNotes());
		response.setCreatedAt(adjustment.getCreatedAt());
		response.setUpdatedAt(adjustment.getUpdatedAt());
		return response;
	}
}
