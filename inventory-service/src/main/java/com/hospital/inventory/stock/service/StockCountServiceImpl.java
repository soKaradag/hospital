package com.hospital.inventory.stock.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockCountRequest;
import com.hospital.inventory.stock.dto.StockCountLineRequest;
import com.hospital.inventory.stock.dto.StockCountResponse;
import com.hospital.inventory.stock.model.MovementType;
import com.hospital.inventory.stock.model.StockAdjustment;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockCount;
import com.hospital.inventory.stock.model.StockCountLine;
import com.hospital.inventory.stock.model.StockCountStatus;
import com.hospital.inventory.stock.model.StockMovement;
import com.hospital.inventory.stock.repository.StockAdjustmentRepository;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockCountLineRepository;
import com.hospital.inventory.stock.repository.StockCountRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@Service
public class StockCountServiceImpl implements StockCountService {

	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockCountRepository stockCountRepository;
	private final StockCountLineRepository stockCountLineRepository;
	private final StockAdjustmentRepository stockAdjustmentRepository;
	private final StockMovementRepository stockMovementRepository;

	public StockCountServiceImpl(
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository,
			InventoryItemRepository inventoryItemRepository,
			StockBatchRepository stockBatchRepository,
			StockCountRepository stockCountRepository,
			StockCountLineRepository stockCountLineRepository,
			StockAdjustmentRepository stockAdjustmentRepository,
			StockMovementRepository stockMovementRepository) {
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
		this.inventoryItemRepository = inventoryItemRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockCountRepository = stockCountRepository;
		this.stockCountLineRepository = stockCountLineRepository;
		this.stockAdjustmentRepository = stockAdjustmentRepository;
		this.stockMovementRepository = stockMovementRepository;
	}

	@Override
	@Transactional
	public StockCountResponse create(CreateStockCountRequest request) {
		Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + request.getWarehouseId()));
		WarehouseZone zone = request.getWarehouseZoneId() != null
				? warehouseZoneRepository.findById(request.getWarehouseZoneId())
						.orElseThrow(() -> new ResourceNotFoundException(
								"Warehouse zone not found: " + request.getWarehouseZoneId()))
				: null;

		StockCount stockCount = new StockCount();
		stockCount.setWarehouse(warehouse);
		stockCount.setWarehouseZone(zone);
		stockCount.setStatus(StockCountStatus.OPEN);
		stockCount.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);
		StockCount savedCount = stockCountRepository.save(stockCount);

		for (StockCountLineRequest lineRequest : request.getLines()) {
			InventoryItem item = inventoryItemRepository.findById(lineRequest.getItemId())
					.orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + lineRequest.getItemId()));
			StockBatch batch = stockBatchRepository.findById(lineRequest.getBatchId())
					.orElseThrow(() -> new ResourceNotFoundException("Stock batch not found: " + lineRequest.getBatchId()));
			if (!batch.getInventoryItem().getId().equals(item.getId())) {
				throw new BusinessRuleViolationException("Batch does not belong to the requested inventory item");
			}

			StockCountLine line = new StockCountLine();
			line.setStockCount(savedCount);
			line.setInventoryItem(item);
			line.setStockBatch(batch);
			line.setExpectedQuantity(batch.getQuantityOnHand());
			line.setCountedQuantity(lineRequest.getCountedQuantity());
			line.setDifferenceQuantity(lineRequest.getCountedQuantity().subtract(batch.getQuantityOnHand()));
			line.setNotes(lineRequest.getNotes() != null ? lineRequest.getNotes().trim() : null);
			stockCountLineRepository.save(line);
		}

		return toResponse(savedCount, stockCountLineRepository.findAllByStockCountId(savedCount.getId()));
	}

	@Override
	@Transactional
	public StockCountResponse close(UUID countId) {
		StockCount stockCount = stockCountRepository.findById(countId)
				.orElseThrow(() -> new ResourceNotFoundException("Stock count not found: " + countId));
		if (stockCount.getStatus() == StockCountStatus.CLOSED) {
			throw new BusinessRuleViolationException("Stock count is already closed");
		}

		List<StockCountLine> lines = stockCountLineRepository.findAllByStockCountId(countId);
		for (StockCountLine line : lines) {
			if (line.getDifferenceQuantity().compareTo(java.math.BigDecimal.ZERO) != 0) {
				StockBatch batch = line.getStockBatch();
				batch.setQuantityOnHand(line.getCountedQuantity());
				stockBatchRepository.save(batch);

				StockAdjustment adjustment = new StockAdjustment();
				adjustment.setInventoryItem(line.getInventoryItem());
				adjustment.setStockBatch(batch);
				adjustment.setWarehouse(stockCount.getWarehouse());
				adjustment.setWarehouseZone(stockCount.getWarehouseZone());
				adjustment.setQuantityDelta(line.getDifferenceQuantity());
				adjustment.setReasonCode("count_close");
				adjustment.setNotes("Auto adjustment from stock count close");
				StockAdjustment savedAdjustment = stockAdjustmentRepository.save(adjustment);

				StockMovement movement = new StockMovement();
				movement.setInventoryItem(line.getInventoryItem());
				movement.setStockBatch(batch);
				movement.setWarehouse(stockCount.getWarehouse());
				movement.setWarehouseZone(stockCount.getWarehouseZone());
				movement.setMovementType(MovementType.ADJUSTMENT);
				movement.setQuantity(line.getDifferenceQuantity());
				movement.setOccurredAt(Instant.now());
				movement.setReferenceType("stock_count");
				movement.setReferenceId(savedAdjustment.getId().toString());
				movement.setNotes("Stock count close adjustment");
				stockMovementRepository.save(movement);
			}
		}

		stockCount.setStatus(StockCountStatus.CLOSED);
		stockCount.setClosedAt(Instant.now());
		stockCountRepository.save(stockCount);
		return toResponse(stockCount, lines);
	}

	private StockCountResponse toResponse(StockCount stockCount, List<StockCountLine> lines) {
		StockCountResponse response = new StockCountResponse();
		response.setId(stockCount.getId());
		response.setWarehouseId(stockCount.getWarehouse().getId());
		response.setWarehouseZoneId(stockCount.getWarehouseZone() != null ? stockCount.getWarehouseZone().getId() : null);
		response.setStatus(stockCount.getStatus().name());
		response.setNotes(stockCount.getNotes());
		response.setClosedAt(stockCount.getClosedAt());
		response.setCreatedAt(stockCount.getCreatedAt());
		response.setUpdatedAt(stockCount.getUpdatedAt());
		response.setLines(lines.stream().map(this::toResponse).toList());
		return response;
	}

	private StockCountResponse.Line toResponse(StockCountLine line) {
		StockCountResponse.Line response = new StockCountResponse.Line();
		response.setId(line.getId());
		response.setItemId(line.getInventoryItem().getId());
		response.setBatchId(line.getStockBatch().getId());
		response.setExpectedQuantity(line.getExpectedQuantity());
		response.setCountedQuantity(line.getCountedQuantity());
		response.setDifferenceQuantity(line.getDifferenceQuantity());
		response.setNotes(line.getNotes());
		return response;
	}
}
