package com.hospital.inventory.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockReservationRequest;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockReservation;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.stock.repository.StockReservationRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@ExtendWith(MockitoExtension.class)
class StockReservationServiceImplTest {

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private StockBatchRepository stockBatchRepository;

	@Mock
	private StockMovementRepository stockMovementRepository;

	@Mock
	private StockReservationRepository stockReservationRepository;

	@Mock
	private WarehouseRepository warehouseRepository;

	@Mock
	private WarehouseZoneRepository warehouseZoneRepository;

	private StockReservationServiceImpl stockReservationService;

	@BeforeEach
	void setUp() {
		StockBatchAllocationService stockBatchAllocationService = new StockBatchAllocationService(
				stockBatchRepository,
				stockReservationRepository);
		stockReservationService = new StockReservationServiceImpl(
				inventoryItemRepository,
				stockBatchRepository,
				stockMovementRepository,
				stockReservationRepository,
				warehouseRepository,
				warehouseZoneRepository,
				stockBatchAllocationService);
	}

	@Test
	void createShouldRejectReservationWhenBatchDoesNotBelongToRequestedLocation() {
		InventoryItem item = item();
		Warehouse warehouse = warehouse();
		Warehouse otherWarehouse = warehouse();
		StockBatch batch = batch(item, otherWarehouse, null, "LOT-1", "5");

		CreateStockReservationRequest request = request(item.getId(), warehouse.getId(), null, new BigDecimal("2"));
		request.setBatchId(batch.getId());

		when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
		when(stockBatchRepository.findById(batch.getId())).thenReturn(Optional.of(batch));

		assertThrows(BusinessRuleViolationException.class, () -> stockReservationService.create(request));

		verify(stockReservationRepository, never()).save(any());
	}

	@Test
	void createShouldRejectReservationWhenSingleBatchCannotSatisfyRequestedQuantity() {
		InventoryItem item = item();
		Warehouse warehouse = warehouse();
		StockBatch firstBatch = batch(item, warehouse, null, "LOT-1", "3");
		StockBatch secondBatch = batch(item, warehouse, null, "LOT-2", "4");

		CreateStockReservationRequest request = request(item.getId(), warehouse.getId(), null, new BigDecimal("5"));

		when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
		when(stockBatchRepository.findAllByItemAndLocationOrderByExpiry(item.getId(), warehouse.getId(), null))
				.thenReturn(List.of(firstBatch, secondBatch));
		when(stockReservationRepository.sumQuantityByBatchIdAndStatus(firstBatch.getId(), ReservationStatus.ACTIVE))
				.thenReturn(BigDecimal.ZERO);
		when(stockReservationRepository.sumQuantityByBatchIdAndStatus(secondBatch.getId(), ReservationStatus.ACTIVE))
				.thenReturn(BigDecimal.ZERO);

		assertThrows(BusinessRuleViolationException.class, () -> stockReservationService.create(request));

		verify(stockReservationRepository, never()).save(any());
	}

	@Test
	void createShouldPersistResolvedSingleBatchWhenRequestDoesNotSpecifyBatch() {
		InventoryItem item = item();
		Warehouse warehouse = warehouse();
		WarehouseZone zone = zone(warehouse);
		StockBatch batch = batch(item, warehouse, zone, "LOT-1", "10");

		CreateStockReservationRequest request = request(item.getId(), warehouse.getId(), zone.getId(), new BigDecimal("2"));

		when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
		when(warehouseZoneRepository.findById(zone.getId())).thenReturn(Optional.of(zone));
		when(stockBatchRepository.findAllByItemAndLocationOrderByExpiry(item.getId(), warehouse.getId(), zone.getId()))
				.thenReturn(List.of(batch));
		when(stockReservationRepository.sumQuantityByBatchIdAndStatus(batch.getId(), ReservationStatus.ACTIVE))
				.thenReturn(BigDecimal.ZERO);
		when(stockReservationRepository.save(any(StockReservation.class))).thenAnswer(invocation -> {
			StockReservation reservation = invocation.getArgument(0);
			if (reservation.getId() == null) {
				reservation.setId(UUID.randomUUID());
			}
			return reservation;
		});

		stockReservationService.create(request);

		ArgumentCaptor<StockReservation> reservationCaptor = ArgumentCaptor.forClass(StockReservation.class);
		verify(stockReservationRepository).save(reservationCaptor.capture());
		assertSame(batch, reservationCaptor.getValue().getStockBatch());
		assertEquals(new BigDecimal("2"), reservationCaptor.getValue().getQuantity());
	}

	private CreateStockReservationRequest request(
			UUID itemId,
			UUID warehouseId,
			UUID warehouseZoneId,
			BigDecimal quantity) {
		CreateStockReservationRequest request = new CreateStockReservationRequest();
		request.setItemId(itemId);
		request.setWarehouseId(warehouseId);
		request.setWarehouseZoneId(warehouseZoneId);
		request.setQuantity(quantity);
		request.setReservationType("SURGERY");
		request.setReferenceType("SURGERY");
		request.setReferenceId(UUID.randomUUID().toString());
		return request;
	}

	private InventoryItem item() {
		InventoryItem item = new InventoryItem();
		item.setId(UUID.randomUUID());
		item.setCode("GENERAL_MED");
		return item;
	}

	private Warehouse warehouse() {
		Warehouse warehouse = new Warehouse();
		warehouse.setId(UUID.randomUUID());
		return warehouse;
	}

	private WarehouseZone zone(Warehouse warehouse) {
		WarehouseZone zone = new WarehouseZone();
		zone.setId(UUID.randomUUID());
		zone.setWarehouse(warehouse);
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
}
