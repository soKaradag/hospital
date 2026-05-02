package com.hospital.inventory.stock.service;

import java.math.BigDecimal;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockTransferRequest;
import com.hospital.inventory.stock.dto.StockTransferResponse;
import com.hospital.inventory.stock.model.MovementType;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockMovement;
import com.hospital.inventory.stock.model.StockTransfer;
import com.hospital.inventory.stock.model.StockTransferRequest;
import com.hospital.inventory.stock.model.StockTransferRequestStatus;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.stock.repository.StockTransferRepository;
import com.hospital.inventory.stock.repository.StockTransferRequestRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@Service
public class StockTransferServiceImpl implements StockTransferService {

	private final InventoryItemRepository inventoryItemRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockMovementRepository stockMovementRepository;
	private final StockTransferRequestRepository stockTransferRequestRepository;
	private final StockTransferRepository stockTransferRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;

	public StockTransferServiceImpl(
			InventoryItemRepository inventoryItemRepository,
			StockBatchRepository stockBatchRepository,
			StockMovementRepository stockMovementRepository,
			StockTransferRequestRepository stockTransferRequestRepository,
			StockTransferRepository stockTransferRepository,
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockMovementRepository = stockMovementRepository;
		this.stockTransferRequestRepository = stockTransferRequestRepository;
		this.stockTransferRepository = stockTransferRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
	}

	@Override
	@Transactional
	public StockTransferResponse create(CreateStockTransferRequest request) {
		InventoryItem item = inventoryItemRepository.findById(request.getItemId())
				.orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + request.getItemId()));
		StockBatch sourceBatch = stockBatchRepository.findById(request.getBatchId())
				.orElseThrow(() -> new ResourceNotFoundException("Stock batch not found: " + request.getBatchId()));
		Warehouse fromWarehouse = warehouseRepository.findById(request.getFromWarehouseId())
				.orElseThrow(() -> new ResourceNotFoundException("Source warehouse not found: " + request.getFromWarehouseId()));
		Warehouse toWarehouse = warehouseRepository.findById(request.getToWarehouseId())
				.orElseThrow(() -> new ResourceNotFoundException("Destination warehouse not found: " + request.getToWarehouseId()));
		WarehouseZone fromZone = request.getFromWarehouseZoneId() != null
				? warehouseZoneRepository.findById(request.getFromWarehouseZoneId())
						.orElseThrow(() -> new ResourceNotFoundException(
								"Source warehouse zone not found: " + request.getFromWarehouseZoneId()))
				: null;
		WarehouseZone toZone = request.getToWarehouseZoneId() != null
				? warehouseZoneRepository.findById(request.getToWarehouseZoneId())
						.orElseThrow(() -> new ResourceNotFoundException(
								"Destination warehouse zone not found: " + request.getToWarehouseZoneId()))
				: null;

		if (!sourceBatch.getInventoryItem().getId().equals(item.getId())) {
			throw new BusinessRuleViolationException("Source batch does not belong to the requested inventory item");
		}
		if (request.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
			throw new BusinessRuleViolationException("Transfer quantity must be greater than zero");
		}
		if (fromWarehouse.getId().equals(toWarehouse.getId())
				&& (fromZone == null ? toZone == null : fromZone.getId().equals(toZone != null ? toZone.getId() : null))) {
			throw new BusinessRuleViolationException("Source and destination locations must be different");
		}
		if (sourceBatch.getQuantityOnHand().compareTo(request.getQuantity()) < 0) {
			throw new BusinessRuleViolationException("Insufficient stock available for transfer");
		}

		StockTransferRequest transferRequest = new StockTransferRequest();
		transferRequest.setInventoryItem(item);
		transferRequest.setStockBatch(sourceBatch);
		transferRequest.setFromWarehouse(fromWarehouse);
		transferRequest.setFromWarehouseZone(fromZone);
		transferRequest.setToWarehouse(toWarehouse);
		transferRequest.setToWarehouseZone(toZone);
		transferRequest.setQuantity(request.getQuantity());
		transferRequest.setStatus(StockTransferRequestStatus.REQUESTED);
		transferRequest.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);
		StockTransferRequest savedRequest = stockTransferRequestRepository.save(transferRequest);

		sourceBatch.setQuantityOnHand(sourceBatch.getQuantityOnHand().subtract(request.getQuantity()));
		stockBatchRepository.save(sourceBatch);

		StockBatch destinationBatch = findOrCreateDestinationBatch(sourceBatch, toWarehouse, toZone);
		destinationBatch.setQuantityOnHand(destinationBatch.getQuantityOnHand().add(request.getQuantity()));
		stockBatchRepository.save(destinationBatch);

		stockMovementRepository.save(createMovement(item, sourceBatch, fromWarehouse, fromZone, MovementType.TRANSFER_OUT,
				request.getQuantity(), savedRequest.getId().toString(), request.getNotes()));
		stockMovementRepository.save(createMovement(item, destinationBatch, toWarehouse, toZone, MovementType.TRANSFER_IN,
				request.getQuantity(), savedRequest.getId().toString(), request.getNotes()));

		savedRequest.setStatus(StockTransferRequestStatus.COMPLETED);
		stockTransferRequestRepository.save(savedRequest);

		StockTransfer transfer = new StockTransfer();
		transfer.setTransferRequest(savedRequest);
		transfer.setSourceBatch(sourceBatch);
		transfer.setDestinationBatch(destinationBatch);
		transfer.setQuantity(request.getQuantity());
		transfer.setCompletedAt(Instant.now());
		StockTransfer savedTransfer = stockTransferRepository.save(transfer);
		return toResponse(savedRequest, savedTransfer);
	}

	private StockBatch findOrCreateDestinationBatch(StockBatch sourceBatch, Warehouse toWarehouse, WarehouseZone toZone) {
		return stockBatchRepository.findAllByInventoryItemIdAndActiveTrueOrderByExpiresAtAscBatchNumberAsc(
				sourceBatch.getInventoryItem().getId()).stream()
				.filter(batch -> batch.getWarehouse().getId().equals(toWarehouse.getId()))
				.filter(batch -> batch.getWarehouseZone() == null
						? toZone == null
						: toZone != null && batch.getWarehouseZone().getId().equals(toZone.getId()))
				.filter(batch -> batch.getBatchNumber().equalsIgnoreCase(sourceBatch.getBatchNumber()))
				.findFirst()
				.orElseGet(() -> {
					StockBatch destinationBatch = new StockBatch();
					destinationBatch.setInventoryItem(sourceBatch.getInventoryItem());
					destinationBatch.setWarehouse(toWarehouse);
					destinationBatch.setWarehouseZone(toZone);
					destinationBatch.setBatchNumber(sourceBatch.getBatchNumber());
					destinationBatch.setExpiresAt(sourceBatch.getExpiresAt());
					destinationBatch.setQuantityOnHand(BigDecimal.ZERO);
					destinationBatch.setActive(true);
					return destinationBatch;
				});
	}

	private StockMovement createMovement(
			InventoryItem item,
			StockBatch batch,
			Warehouse warehouse,
			WarehouseZone zone,
			MovementType movementType,
			BigDecimal quantity,
			String referenceId,
			String notes) {
		StockMovement movement = new StockMovement();
		movement.setInventoryItem(item);
		movement.setStockBatch(batch);
		movement.setWarehouse(warehouse);
		movement.setWarehouseZone(zone);
		movement.setMovementType(movementType);
		movement.setQuantity(quantity);
		movement.setOccurredAt(Instant.now());
		movement.setReferenceType("transfer");
		movement.setReferenceId(referenceId);
		movement.setNotes(notes != null ? notes.trim() : null);
		return movement;
	}

	private StockTransferResponse toResponse(StockTransferRequest request, StockTransfer transfer) {
		StockTransferResponse response = new StockTransferResponse();
		response.setRequestId(request.getId());
		response.setTransferId(transfer.getId());
		response.setItemId(request.getInventoryItem().getId());
		response.setSourceBatchId(transfer.getSourceBatch().getId());
		response.setDestinationBatchId(transfer.getDestinationBatch().getId());
		response.setFromWarehouseId(request.getFromWarehouse().getId());
		response.setFromWarehouseZoneId(request.getFromWarehouseZone() != null ? request.getFromWarehouseZone().getId() : null);
		response.setToWarehouseId(request.getToWarehouse().getId());
		response.setToWarehouseZoneId(request.getToWarehouseZone() != null ? request.getToWarehouseZone().getId() : null);
		response.setQuantity(request.getQuantity());
		response.setStatus(request.getStatus().name());
		response.setCompletedAt(transfer.getCompletedAt());
		return response;
	}
}
