package com.hospital.inventory.stock.service;

import java.util.List;
import java.util.UUID;

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

	public SurgeryStockReservationServiceImpl(
			InventoryItemRepository inventoryItemRepository,
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository,
			StockReservationRepository stockReservationRepository,
			StockReservationService stockReservationService) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
		this.stockReservationRepository = stockReservationRepository;
		this.stockReservationService = stockReservationService;
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

		List<SurgeryStockReservationResponse.ReservationLine> lines = request.getItems().stream()
				.map(itemRequest -> createReservationLine(request, warehouse, warehouseZone, itemRequest))
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

	private SurgeryStockReservationResponse.ReservationLine createReservationLine(
			CreateSurgeryStockReservationRequest request,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			SurgeryStockReservationItemRequest itemRequest) {
		InventoryItem item = inventoryItemRepository.findByCodeIgnoreCase(itemRequest.getInventoryItemCode().trim())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Inventory item not found: " + itemRequest.getInventoryItemCode()));
		CreateStockReservationRequest createRequest = new CreateStockReservationRequest();
		createRequest.setItemId(item.getId());
		createRequest.setWarehouseId(warehouse.getId());
		createRequest.setWarehouseZoneId(warehouseZone != null ? warehouseZone.getId() : null);
		createRequest.setQuantity(itemRequest.getQuantity());
		createRequest.setReservationType("SURGERY");
		createRequest.setReferenceType(SURGERY_REFERENCE_TYPE);
		createRequest.setReferenceId(request.getSurgeryId().toString());
		createRequest.setExpiresAt(request.getExpiresAt());
		createRequest.setNotes(itemRequest.getNotes());
		StockReservationResponse created = stockReservationService.create(createRequest);
		return toLine(created, item.getCode());
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
}
