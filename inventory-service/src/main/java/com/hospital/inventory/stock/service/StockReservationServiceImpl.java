package com.hospital.inventory.stock.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockReservationRequest;
import com.hospital.inventory.stock.dto.StockReservationResponse;
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
public class StockReservationServiceImpl implements StockReservationService {

	private final InventoryItemRepository inventoryItemRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockMovementRepository stockMovementRepository;
	private final StockReservationRepository stockReservationRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;

	public StockReservationServiceImpl(
			InventoryItemRepository inventoryItemRepository,
			StockBatchRepository stockBatchRepository,
			StockMovementRepository stockMovementRepository,
			StockReservationRepository stockReservationRepository,
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockMovementRepository = stockMovementRepository;
		this.stockReservationRepository = stockReservationRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
	}

	@Override
	@Transactional
	public StockReservationResponse create(CreateStockReservationRequest request) {
		InventoryItem item = inventoryItemRepository.findById(request.getItemId())
				.orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + request.getItemId()));
		Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + request.getWarehouseId()));
		WarehouseZone zone = request.getWarehouseZoneId() != null
				? warehouseZoneRepository.findById(request.getWarehouseZoneId())
						.orElseThrow(() -> new ResourceNotFoundException(
								"Warehouse zone not found: " + request.getWarehouseZoneId()))
				: null;

		StockBatch batch = resolveBatch(request.getBatchId(), item.getId());
		validateAvailability(item.getId(), batch != null ? batch.getId() : null, request.getQuantity());

		StockReservation reservation = new StockReservation();
		reservation.setInventoryItem(item);
		reservation.setStockBatch(batch);
		reservation.setWarehouse(warehouse);
		reservation.setWarehouseZone(zone);
		reservation.setQuantity(request.getQuantity());
		reservation.setStatus(ReservationStatus.ACTIVE);
		reservation.setReservationType(request.getReservationType().trim());
		reservation.setReferenceType(request.getReferenceType() != null ? request.getReferenceType().trim() : null);
		reservation.setReferenceId(request.getReferenceId() != null ? request.getReferenceId().trim() : null);
		reservation.setExpiresAt(request.getExpiresAt());
		reservation.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);

		StockReservation savedReservation = stockReservationRepository.save(reservation);
		stockMovementRepository.save(createMovement(savedReservation, MovementType.RESERVATION, "Reservation created"));
		return toResponse(savedReservation);
	}

	@Override
	@Transactional
	public StockReservationResponse release(UUID reservationId) {
		StockReservation reservation = stockReservationRepository.findByIdAndStatus(reservationId, ReservationStatus.ACTIVE)
				.orElseThrow(() -> new ResourceNotFoundException("Active stock reservation not found: " + reservationId));
		reservation.setStatus(ReservationStatus.RELEASED);
		stockMovementRepository.save(createMovement(reservation, MovementType.RELEASE, "Reservation released"));
		return toResponse(stockReservationRepository.save(reservation));
	}

	private StockBatch resolveBatch(UUID batchId, UUID itemId) {
		if (batchId == null) {
			return null;
		}
		StockBatch batch = stockBatchRepository.findById(batchId)
				.orElseThrow(() -> new ResourceNotFoundException("Stock batch not found: " + batchId));
		if (!batch.getInventoryItem().getId().equals(itemId)) {
			throw new BusinessRuleViolationException("Batch does not belong to the requested inventory item");
		}
		return batch;
	}

	private void validateAvailability(UUID itemId, UUID batchId, BigDecimal requestedQuantity) {
		if (batchId != null) {
			StockBatch batch = stockBatchRepository.findById(batchId)
					.orElseThrow(() -> new ResourceNotFoundException("Stock batch not found: " + batchId));
			BigDecimal reservedQuantity = stockReservationRepository.sumQuantityByBatchIdAndStatus(batchId,
					ReservationStatus.ACTIVE);
			BigDecimal availableQuantity = batch.getQuantityOnHand().subtract(reservedQuantity);
			if (availableQuantity.compareTo(requestedQuantity) < 0) {
				throw new BusinessRuleViolationException("Insufficient stock available in the requested batch");
			}
			return;
		}

		BigDecimal totalOnHand = stockBatchRepository.sumQuantityOnHandByItemId(itemId);
		BigDecimal reservedQuantity = stockReservationRepository.sumQuantityByItemIdAndStatus(itemId,
				ReservationStatus.ACTIVE);
		BigDecimal availableQuantity = totalOnHand.subtract(reservedQuantity);
		if (availableQuantity.compareTo(requestedQuantity) < 0) {
			throw new BusinessRuleViolationException("Insufficient stock available for reservation");
		}
	}

	private StockMovement createMovement(StockReservation reservation, MovementType movementType, String notes) {
		StockMovement movement = new StockMovement();
		movement.setInventoryItem(reservation.getInventoryItem());
		movement.setStockBatch(reservation.getStockBatch());
		movement.setWarehouse(reservation.getWarehouse());
		movement.setWarehouseZone(reservation.getWarehouseZone());
		movement.setMovementType(movementType);
		movement.setQuantity(reservation.getQuantity());
		movement.setOccurredAt(Instant.now());
		movement.setReferenceType(reservation.getReservationType());
		movement.setReferenceId(reservation.getId().toString());
		movement.setNotes(notes);
		return movement;
	}

	private StockReservationResponse toResponse(StockReservation reservation) {
		StockReservationResponse response = new StockReservationResponse();
		response.setId(reservation.getId());
		response.setItemId(reservation.getInventoryItem().getId());
		response.setBatchId(reservation.getStockBatch() != null ? reservation.getStockBatch().getId() : null);
		response.setWarehouseId(reservation.getWarehouse().getId());
		response.setWarehouseZoneId(reservation.getWarehouseZone() != null ? reservation.getWarehouseZone().getId() : null);
		response.setQuantity(reservation.getQuantity());
		response.setStatus(reservation.getStatus().name());
		response.setReservationType(reservation.getReservationType());
		response.setReferenceType(reservation.getReferenceType());
		response.setReferenceId(reservation.getReferenceId());
		response.setExpiresAt(reservation.getExpiresAt());
		response.setNotes(reservation.getNotes());
		response.setCreatedAt(reservation.getCreatedAt());
		response.setUpdatedAt(reservation.getUpdatedAt());
		return response;
	}
}
