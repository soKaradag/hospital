package com.hospital.inventory.stock.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockReservationRequest;
import com.hospital.inventory.stock.dto.CreateSurgeryStockReservationRequest;
import com.hospital.inventory.stock.dto.StockReservationResponse;
import com.hospital.inventory.stock.dto.SurgeryStockReservationItemRequest;
import com.hospital.inventory.stock.dto.SurgeryStockReservationResponse;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.model.StockReservation;
import com.hospital.inventory.stock.repository.StockReservationRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@Service
public class SurgeryStockReservationServiceImpl implements SurgeryStockReservationService {

	private static final String SURGERY_REFERENCE_TYPE = "SURGERY";

	private final InventoryItemRepository inventoryItemRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;
	private final StockReservationRepository stockReservationRepository;
	private final StockReservationService stockReservationService;
	private final StockBatchAllocationService stockBatchAllocationService;

	public SurgeryStockReservationServiceImpl(
			InventoryItemRepository inventoryItemRepository,
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository,
			StockReservationRepository stockReservationRepository,
			StockReservationService stockReservationService,
			StockBatchAllocationService stockBatchAllocationService) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
		this.stockReservationRepository = stockReservationRepository;
		this.stockReservationService = stockReservationService;
		this.stockBatchAllocationService = stockBatchAllocationService;
	}

	@Override
	@Transactional
	public SurgeryStockReservationResponse createReservations(CreateSurgeryStockReservationRequest request) {
		if (!stockReservationRepository.findAllByReferenceTypeAndReferenceIdAndStatus(
				SURGERY_REFERENCE_TYPE,
				request.getSurgeryId().toString(),
				ReservationStatus.ACTIVE).isEmpty()) {
			throw new BusinessRuleViolationException("Active surgery reservations already exist for this surgery");
		}
		Warehouse warehouse = warehouseRepository.findByCodeIgnoreCase(request.getWarehouseCode().trim())
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + request.getWarehouseCode()));
			WarehouseZone warehouseZone = getWarehouseZone(request.getWarehouseZoneCode(), warehouse.getId());

			Map<UUID, java.math.BigDecimal> pendingReservedQuantities = new HashMap<>();
			List<PendingReservation> pendingReservations = request.getItems().stream()
					.flatMap(itemRequest -> createPendingReservations(
							request,
							warehouse,
							warehouseZone,
							itemRequest,
							pendingReservedQuantities).stream())
					.toList();
			List<SurgeryStockReservationResponse.ReservationLine> lines = pendingReservations.stream()
					.map(this::createReservationLine)
					.toList();
			SurgeryStockReservationResponse response = new SurgeryStockReservationResponse();
			response.setSurgeryId(request.getSurgeryId());
			response.setStatus("RESERVED");
		response.setReservations(lines);
		return response;
	}

	@Override
	@Transactional
	public SurgeryStockReservationResponse releaseReservations(UUID surgeryId) {
		List<StockReservation> activeReservations = stockReservationRepository.findAllByReferenceTypeAndReferenceIdAndStatus(
				SURGERY_REFERENCE_TYPE,
				surgeryId.toString(),
				ReservationStatus.ACTIVE);
		List<SurgeryStockReservationResponse.ReservationLine> lines = activeReservations.stream()
				.map(reservation -> toLine(stockReservationService.release(reservation.getId()), reservation.getInventoryItem().getCode()))
				.toList();
		SurgeryStockReservationResponse response = new SurgeryStockReservationResponse();
		response.setSurgeryId(surgeryId);
		response.setStatus(lines.isEmpty() ? "NO_ACTIVE_RESERVATION" : "RELEASED");
		response.setReservations(lines);
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public SurgeryStockReservationResponse getReservationStatus(UUID surgeryId) {
		List<SurgeryStockReservationResponse.ReservationLine> lines = stockReservationRepository
				.findAllByReferenceTypeAndReferenceId(SURGERY_REFERENCE_TYPE, surgeryId.toString()).stream()
				.map(this::toLine)
				.toList();
		SurgeryStockReservationResponse response = new SurgeryStockReservationResponse();
		response.setSurgeryId(surgeryId);
		response.setStatus(lines.stream().anyMatch(line -> "ACTIVE".equalsIgnoreCase(line.getStatus()))
				? "ACTIVE"
				: "INACTIVE");
		response.setReservations(lines);
		return response;
	}

	private List<PendingReservation> createPendingReservations(
			CreateSurgeryStockReservationRequest request,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			SurgeryStockReservationItemRequest itemRequest,
			Map<UUID, java.math.BigDecimal> pendingReservedQuantities) {
		InventoryItem item = inventoryItemRepository.findByCodeIgnoreCase(itemRequest.getInventoryItemCode().trim())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Inventory item not found: " + itemRequest.getInventoryItemCode()));
		return stockBatchAllocationService.allocateAcrossBatches(
				item,
				warehouse,
				warehouseZone,
				itemRequest.getQuantity(),
				pendingReservedQuantities).stream()
				.map(allocation -> new PendingReservation(
						item.getId(),
						allocation.batch().getId(),
						allocation.quantity(),
						item.getCode(),
						itemRequest.getNotes(),
						request.getExpiresAt(),
						warehouse.getId(),
						warehouseZone != null ? warehouseZone.getId() : null,
						request.getSurgeryId().toString()))
				.toList();
	}

	private SurgeryStockReservationResponse.ReservationLine createReservationLine(PendingReservation pendingReservation) {
		CreateStockReservationRequest createRequest = new CreateStockReservationRequest();
		createRequest.setItemId(pendingReservation.itemId());
		createRequest.setBatchId(pendingReservation.batchId());
		createRequest.setWarehouseId(pendingReservation.warehouseId());
		createRequest.setWarehouseZoneId(pendingReservation.warehouseZoneId());
		createRequest.setQuantity(pendingReservation.quantity());
		createRequest.setReservationType("SURGERY");
		createRequest.setReferenceType(SURGERY_REFERENCE_TYPE);
		createRequest.setReferenceId(pendingReservation.referenceId());
		createRequest.setExpiresAt(pendingReservation.expiresAt());
		createRequest.setNotes(pendingReservation.notes());
		StockReservationResponse created = stockReservationService.create(createRequest);
		return toLine(created, pendingReservation.inventoryItemCode());
	}

	private WarehouseZone getWarehouseZone(String warehouseZoneCode, UUID warehouseId) {
		if (warehouseZoneCode == null || warehouseZoneCode.isBlank()) {
			return null;
		}
		return warehouseZoneRepository.findByWarehouseIdAndCodeIgnoreCase(warehouseId, warehouseZoneCode.trim())
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse zone not found: " + warehouseZoneCode));
	}

	private SurgeryStockReservationResponse.ReservationLine toLine(StockReservationResponse reservation, String inventoryItemCode) {
		SurgeryStockReservationResponse.ReservationLine line = new SurgeryStockReservationResponse.ReservationLine();
		line.setReservationId(reservation.getId());
		line.setInventoryItemCode(inventoryItemCode);
		line.setQuantity(reservation.getQuantity());
		line.setStatus(reservation.getStatus());
		return line;
	}

	private SurgeryStockReservationResponse.ReservationLine toLine(StockReservation reservation) {
		SurgeryStockReservationResponse.ReservationLine line = new SurgeryStockReservationResponse.ReservationLine();
		line.setReservationId(reservation.getId());
		line.setInventoryItemCode(reservation.getInventoryItem().getCode());
		line.setQuantity(reservation.getQuantity());
		line.setStatus(reservation.getStatus().name());
		return line;
	}

	private record PendingReservation(
			UUID itemId,
			UUID batchId,
			java.math.BigDecimal quantity,
			String inventoryItemCode,
			String notes,
			java.time.Instant expiresAt,
			UUID warehouseId,
			UUID warehouseZoneId,
			String referenceId) {
	}
}
