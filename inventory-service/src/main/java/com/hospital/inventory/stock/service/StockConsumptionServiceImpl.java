package com.hospital.inventory.stock.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockConsumptionRequest;
import com.hospital.inventory.stock.dto.StockConsumptionResponse;
import com.hospital.inventory.stock.model.MovementType;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockMovement;
import com.hospital.inventory.stock.model.StockReservation;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.stock.repository.StockReservationRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@Service
public class StockConsumptionServiceImpl implements StockConsumptionService {

	private final InventoryItemRepository inventoryItemRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockReservationRepository stockReservationRepository;
	private final StockMovementRepository stockMovementRepository;
	private static final String SURGERY_REFERENCE_TYPE = "surgery";

	public StockConsumptionServiceImpl(
			InventoryItemRepository inventoryItemRepository,
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository,
			StockBatchRepository stockBatchRepository,
			StockReservationRepository stockReservationRepository,
			StockMovementRepository stockMovementRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockReservationRepository = stockReservationRepository;
		this.stockMovementRepository = stockMovementRepository;
	}

	@Override
	@Transactional
	public StockConsumptionResponse consume(CreateStockConsumptionRequest request) {
		InventoryItem inventoryItem = inventoryItemRepository.findByCodeIgnoreCase(request.getInventoryItemCode().trim())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Inventory item not found: " + request.getInventoryItemCode()));
		Warehouse warehouse = warehouseRepository.findByCodeIgnoreCase(request.getWarehouseCode().trim())
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + request.getWarehouseCode()));
		WarehouseZone warehouseZone = getWarehouseZone(request.getWarehouseZoneCode(), warehouse.getId());

		List<StockBatch> candidateBatches = resolveCandidateBatches(inventoryItem, warehouse, warehouseZone, request.getBatchNumber());
		BigDecimal remaining = request.getQuantity();
		List<StockConsumptionResponse.Line> lines = new ArrayList<>();
		Instant occurredAt = Instant.now();
		remaining = consumeReservedSurgeryStockIfNeeded(
				request,
				inventoryItem,
				warehouse,
				warehouseZone,
				remaining,
				occurredAt,
				lines);

		for (StockBatch batch : candidateBatches) {
			BigDecimal reserved = stockReservationRepository.sumQuantityByBatchIdAndStatus(batch.getId(), ReservationStatus.ACTIVE);
			BigDecimal available = batch.getQuantityOnHand().subtract(reserved);
			if (available.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			BigDecimal consumeQuantity = available.min(remaining);
			if (consumeQuantity.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			batch.setQuantityOnHand(batch.getQuantityOnHand().subtract(consumeQuantity));
			if (batch.getQuantityOnHand().compareTo(BigDecimal.ZERO) <= 0) {
				batch.setActive(false);
			}
			stockBatchRepository.save(batch);

			StockMovement movement = new StockMovement();
			movement.setInventoryItem(inventoryItem);
			movement.setStockBatch(batch);
			movement.setWarehouse(warehouse);
				movement.setWarehouseZone(warehouseZone);
				movement.setMovementType(MovementType.CONSUMPTION);
				movement.setQuantity(consumeQuantity);
				movement.setOccurredAt(occurredAt);
				movement.setReferenceType(trimToNull(request.getReferenceType()));
				movement.setReferenceId(trimToNull(request.getReferenceId()));
				movement.setNotes(trimToNull(request.getNotes()));
				stockMovementRepository.save(movement);

				lines.add(toLine(batch, consumeQuantity));
				remaining = remaining.subtract(consumeQuantity);
				if (remaining.compareTo(BigDecimal.ZERO) == 0) {
					break;
			}
		}

		if (remaining.compareTo(BigDecimal.ZERO) > 0) {
			throw new BusinessRuleViolationException("Insufficient stock for inventory item " + inventoryItem.getCode());
		}

		StockConsumptionResponse response = new StockConsumptionResponse();
		response.setInventoryItemCode(inventoryItem.getCode());
		response.setWarehouseCode(warehouse.getCode());
		response.setWarehouseZoneCode(warehouseZone != null ? warehouseZone.getCode() : null);
		response.setRequestedQuantity(request.getQuantity());
		response.setConsumedQuantity(request.getQuantity().subtract(remaining));
		response.setLines(lines);
		return response;
	}

	private BigDecimal consumeReservedSurgeryStockIfNeeded(
			CreateStockConsumptionRequest request,
			InventoryItem inventoryItem,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			BigDecimal remaining,
			Instant occurredAt,
			List<StockConsumptionResponse.Line> lines) {
		String referenceType = trimToNull(request.getReferenceType());
		String referenceId = trimToNull(request.getReferenceId());
		if (!SURGERY_REFERENCE_TYPE.equalsIgnoreCase(referenceType) || referenceId == null) {
			return remaining;
		}
		List<StockReservation> reservations = stockReservationRepository
				.findAllByReferenceTypeAndReferenceIdAndStatus(referenceType, referenceId, ReservationStatus.ACTIVE)
				.stream()
				.filter(reservation -> reservation.getStockBatch() != null)
				.filter(reservation -> reservation.getInventoryItem().getId().equals(inventoryItem.getId()))
				.filter(reservation -> reservation.getWarehouse().getId().equals(warehouse.getId()))
				.filter(reservation -> sameZone(reservation.getWarehouseZone(), warehouseZone))
				.sorted(Comparator
						.comparing((StockReservation reservation) -> reservation.getStockBatch().getExpiresAt(),
								Comparator.nullsLast(Comparator.naturalOrder()))
						.thenComparing(reservation -> reservation.getStockBatch().getBatchNumber()))
				.toList();
		for (StockReservation reservation : reservations) {
			if (remaining.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}
			StockBatch batch = reservation.getStockBatch();
			BigDecimal consumeQuantity = batch.getQuantityOnHand().min(reservation.getQuantity()).min(remaining);
			if (consumeQuantity.compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			batch.setQuantityOnHand(batch.getQuantityOnHand().subtract(consumeQuantity));
			if (batch.getQuantityOnHand().compareTo(BigDecimal.ZERO) <= 0) {
				batch.setActive(false);
			}
			stockBatchRepository.save(batch);
			stockMovementRepository.save(createConsumptionMovement(
					inventoryItem,
					batch,
					warehouse,
					warehouseZone,
					consumeQuantity,
					occurredAt,
					referenceType,
					referenceId,
					trimToNull(request.getNotes())));
			lines.add(toLine(batch, consumeQuantity));
			remaining = remaining.subtract(consumeQuantity);
			if (reservation.getQuantity().compareTo(consumeQuantity) == 0) {
				reservation.setStatus(ReservationStatus.CONSUMED);
			} else {
				reservation.setQuantity(reservation.getQuantity().subtract(consumeQuantity));
			}
			stockReservationRepository.save(reservation);
		}
		return remaining;
	}

	private WarehouseZone getWarehouseZone(String zoneCode, java.util.UUID warehouseId) {
		if (zoneCode == null || zoneCode.isBlank()) {
			return null;
		}
		return warehouseZoneRepository.findByWarehouseIdAndCodeIgnoreCase(warehouseId, zoneCode.trim())
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse zone not found: " + zoneCode));
	}

	private List<StockBatch> resolveCandidateBatches(
			InventoryItem inventoryItem,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			String batchNumber) {
		if (batchNumber != null && !batchNumber.isBlank()) {
			StockBatch batch = stockBatchRepository.findMatchingBatch(
					inventoryItem.getId(),
					warehouse.getId(),
					warehouseZone != null ? warehouseZone.getId() : null,
					batchNumber.trim(),
					null).orElseThrow(() -> new ResourceNotFoundException("Stock batch not found: " + batchNumber));
			return List.of(batch);
		}
		return stockBatchRepository.findAllByItemAndLocationOrderByExpiry(
				inventoryItem.getId(),
				warehouse.getId(),
				warehouseZone != null ? warehouseZone.getId() : null);
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isBlank() ? null : trimmed;
	}

	private StockMovement createConsumptionMovement(
			InventoryItem inventoryItem,
			StockBatch batch,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			BigDecimal quantity,
			Instant occurredAt,
			String referenceType,
			String referenceId,
			String notes) {
		StockMovement movement = new StockMovement();
		movement.setInventoryItem(inventoryItem);
		movement.setStockBatch(batch);
		movement.setWarehouse(warehouse);
		movement.setWarehouseZone(warehouseZone);
		movement.setMovementType(MovementType.CONSUMPTION);
		movement.setQuantity(quantity);
		movement.setOccurredAt(occurredAt);
		movement.setReferenceType(referenceType);
		movement.setReferenceId(referenceId);
		movement.setNotes(notes);
		return movement;
	}

	private StockConsumptionResponse.Line toLine(StockBatch batch, BigDecimal quantity) {
		StockConsumptionResponse.Line line = new StockConsumptionResponse.Line();
		line.setStockBatchId(batch.getId());
		line.setBatchNumber(batch.getBatchNumber());
		line.setQuantity(quantity);
		return line;
	}

	private boolean sameZone(WarehouseZone left, WarehouseZone right) {
		if (left == null || right == null) {
			return left == right;
		}
		return left.getId().equals(right.getId());
	}
}
