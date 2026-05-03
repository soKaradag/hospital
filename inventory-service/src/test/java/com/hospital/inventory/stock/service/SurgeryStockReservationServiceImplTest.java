package com.hospital.inventory.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockReservationRequest;
import com.hospital.inventory.stock.dto.CreateSurgeryStockReservationRequest;
import com.hospital.inventory.stock.dto.StockReservationResponse;
import com.hospital.inventory.stock.dto.SurgeryStockReservationItemRequest;
import com.hospital.inventory.stock.dto.SurgeryStockReservationResponse;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockReservation;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockReservationRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@ExtendWith(MockitoExtension.class)
class SurgeryStockReservationServiceImplTest {

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private WarehouseRepository warehouseRepository;

	@Mock
	private WarehouseZoneRepository warehouseZoneRepository;

	@Mock
	private StockReservationRepository stockReservationRepository;

	@Mock
	private StockReservationService stockReservationService;

	@Mock
	private StockBatchRepository stockBatchRepository;

	private SurgeryStockReservationServiceImpl surgeryStockReservationService;

	@BeforeEach
	void setUp() {
		StockBatchAllocationService stockBatchAllocationService = new StockBatchAllocationService(
				stockBatchRepository,
				stockReservationRepository);
		surgeryStockReservationService = new SurgeryStockReservationServiceImpl(
				inventoryItemRepository,
				warehouseRepository,
				warehouseZoneRepository,
				stockReservationRepository,
				stockReservationService,
				stockBatchAllocationService);
	}

	@Test
	void createReservationsShouldSplitAcrossConcreteBatches() {
		UUID surgeryId = UUID.randomUUID();
		Warehouse warehouse = warehouse("SURGERY");
		WarehouseZone zone = zone(warehouse, "ROOM-1");
		InventoryItem item = item("GENERAL_MED");
		StockBatch firstBatch = batch(item, warehouse, zone, "LOT-1", "2");
		StockBatch secondBatch = batch(item, warehouse, zone, "LOT-2", "3");

		CreateSurgeryStockReservationRequest request = request(surgeryId, warehouse.getCode(), zone.getCode(), "GENERAL_MED", "5");

		when(stockReservationRepository.findAllByReferenceTypeAndReferenceIdAndStatus("SURGERY", surgeryId.toString(), ReservationStatus.ACTIVE))
				.thenReturn(List.of());
		when(warehouseRepository.findByCodeIgnoreCase(warehouse.getCode())).thenReturn(Optional.of(warehouse));
		when(warehouseZoneRepository.findByWarehouseIdAndCodeIgnoreCase(warehouse.getId(), zone.getCode()))
				.thenReturn(Optional.of(zone));
		when(inventoryItemRepository.findByCodeIgnoreCase(item.getCode())).thenReturn(Optional.of(item));
		when(stockBatchRepository.findAllByItemAndLocationOrderByExpiry(item.getId(), warehouse.getId(), zone.getId()))
				.thenReturn(List.of(firstBatch, secondBatch));
		when(stockReservationRepository.sumQuantityByBatchIdAndStatus(firstBatch.getId(), ReservationStatus.ACTIVE))
				.thenReturn(BigDecimal.ZERO);
		when(stockReservationRepository.sumQuantityByBatchIdAndStatus(secondBatch.getId(), ReservationStatus.ACTIVE))
				.thenReturn(BigDecimal.ZERO);
		when(stockReservationService.create(any(CreateStockReservationRequest.class))).thenAnswer(invocation -> {
			CreateStockReservationRequest reservationRequest = invocation.getArgument(0);
			StockReservationResponse response = new StockReservationResponse();
			response.setId(UUID.randomUUID());
			response.setBatchId(reservationRequest.getBatchId());
			response.setQuantity(reservationRequest.getQuantity());
			response.setStatus("ACTIVE");
			return response;
		});

		SurgeryStockReservationResponse response = surgeryStockReservationService.createReservations(request);

		assertEquals("RESERVED", response.getStatus());
		assertEquals(2, response.getReservations().size());
		assertEquals(new BigDecimal("2"), response.getReservations().get(0).getQuantity());
		assertEquals(new BigDecimal("3"), response.getReservations().get(1).getQuantity());
	}

	@Test
	void createReservationsShouldFailWhenStockWouldRequirePartialUnallocatedRemainder() {
		UUID surgeryId = UUID.randomUUID();
		Warehouse warehouse = warehouse("SURGERY");
		WarehouseZone zone = zone(warehouse, "ROOM-1");
		InventoryItem item = item("GENERAL_MED");
		StockBatch batch = batch(item, warehouse, zone, "LOT-1", "2");

		CreateSurgeryStockReservationRequest request = request(surgeryId, warehouse.getCode(), zone.getCode(), "GENERAL_MED", "3");

		when(stockReservationRepository.findAllByReferenceTypeAndReferenceIdAndStatus("SURGERY", surgeryId.toString(), ReservationStatus.ACTIVE))
				.thenReturn(List.of());
		when(warehouseRepository.findByCodeIgnoreCase(warehouse.getCode())).thenReturn(Optional.of(warehouse));
		when(warehouseZoneRepository.findByWarehouseIdAndCodeIgnoreCase(warehouse.getId(), zone.getCode()))
				.thenReturn(Optional.of(zone));
		when(inventoryItemRepository.findByCodeIgnoreCase(item.getCode())).thenReturn(Optional.of(item));
		when(stockBatchRepository.findAllByItemAndLocationOrderByExpiry(item.getId(), warehouse.getId(), zone.getId()))
				.thenReturn(List.of(batch));
		when(stockReservationRepository.sumQuantityByBatchIdAndStatus(batch.getId(), ReservationStatus.ACTIVE))
				.thenReturn(BigDecimal.ZERO);

		assertThrows(BusinessRuleViolationException.class, () -> surgeryStockReservationService.createReservations(request));
		verify(stockReservationService, org.mockito.Mockito.never()).create(any(CreateStockReservationRequest.class));
	}

	@Test
	void getReservationStatusShouldReturnActiveWhenAnyActiveReservationExists() {
		UUID surgeryId = UUID.randomUUID();
		when(stockReservationRepository.findAllByReferenceTypeAndReferenceId("SURGERY", surgeryId.toString()))
				.thenReturn(List.of(reservation(ReservationStatus.ACTIVE)));

		SurgeryStockReservationResponse response = surgeryStockReservationService.getReservationStatus(surgeryId);

		assertEquals("ACTIVE", response.getStatus());
		assertEquals(1, response.getReservations().size());
	}

	@Test
	void getReservationStatusShouldReturnInactiveWhenReservationsAreReleased() {
		UUID surgeryId = UUID.randomUUID();
		when(stockReservationRepository.findAllByReferenceTypeAndReferenceId("SURGERY", surgeryId.toString()))
				.thenReturn(List.of(reservation(ReservationStatus.RELEASED)));

		SurgeryStockReservationResponse response = surgeryStockReservationService.getReservationStatus(surgeryId);

		assertEquals("INACTIVE", response.getStatus());
		assertEquals("RELEASED", response.getReservations().getFirst().getStatus());
	}

	private CreateSurgeryStockReservationRequest request(
			UUID surgeryId,
			String warehouseCode,
			String warehouseZoneCode,
			String inventoryItemCode,
			String quantity) {
		CreateSurgeryStockReservationRequest request = new CreateSurgeryStockReservationRequest();
		request.setSurgeryId(surgeryId);
		request.setWarehouseCode(warehouseCode);
		request.setWarehouseZoneCode(warehouseZoneCode);
		request.setExpiresAt(Instant.now().plusSeconds(3600));
		SurgeryStockReservationItemRequest itemRequest = new SurgeryStockReservationItemRequest();
		itemRequest.setInventoryItemCode(inventoryItemCode);
		itemRequest.setQuantity(new BigDecimal(quantity));
		request.setItems(List.of(itemRequest));
		return request;
	}

	private InventoryItem item(String code) {
		InventoryItem item = new InventoryItem();
		item.setId(UUID.randomUUID());
		item.setCode(code);
		return item;
	}

	private Warehouse warehouse(String code) {
		Warehouse warehouse = new Warehouse();
		warehouse.setId(UUID.randomUUID());
		warehouse.setCode(code);
		return warehouse;
	}

	private WarehouseZone zone(Warehouse warehouse, String code) {
		WarehouseZone zone = new WarehouseZone();
		zone.setId(UUID.randomUUID());
		zone.setWarehouse(warehouse);
		zone.setCode(code);
		return zone;
	}

	private StockBatch batch(
			InventoryItem item,
			Warehouse warehouse,
			WarehouseZone zone,
			String batchNumber,
			String quantityOnHand) {
		StockBatch batch = new StockBatch();
		batch.setId(UUID.randomUUID());
		batch.setInventoryItem(item);
		batch.setWarehouse(warehouse);
		batch.setWarehouseZone(zone);
		batch.setBatchNumber(batchNumber);
		batch.setQuantityOnHand(new BigDecimal(quantityOnHand));
		batch.setActive(true);
		return batch;
	}

	private StockReservation reservation(ReservationStatus status) {
		InventoryItem item = item("GENERAL_MED");
		StockReservation reservation = new StockReservation();
		reservation.setId(UUID.randomUUID());
		reservation.setInventoryItem(item);
		reservation.setQuantity(new BigDecimal("2"));
		reservation.setStatus(status);
		return reservation;
	}
}
