package com.hospital.inventory.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockConsumptionRequest;
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
class StockConsumptionServiceImplTest {

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private WarehouseRepository warehouseRepository;

	@Mock
	private WarehouseZoneRepository warehouseZoneRepository;

	@Mock
	private StockBatchRepository stockBatchRepository;

	@Mock
	private StockReservationRepository stockReservationRepository;

	@Mock
	private StockMovementRepository stockMovementRepository;

	private StockConsumptionServiceImpl stockConsumptionService;

	@BeforeEach
	void setUp() {
		stockConsumptionService = new StockConsumptionServiceImpl(
				inventoryItemRepository,
				warehouseRepository,
				warehouseZoneRepository,
				stockBatchRepository,
				stockReservationRepository,
				stockMovementRepository);
	}

	@Test
	void consumeShouldUseReservedSurgeryBatchBeforeFallbackAndMarkReservationConsumed() {
		InventoryItem item = item("GENERAL_MED");
		Warehouse warehouse = warehouse("SURGERY");
		WarehouseZone zone = zone(warehouse, "ROOM-1");
		StockBatch batch = batch(item, warehouse, zone, "LOT-1", "5");
		StockReservation reservation = reservation(item, warehouse, zone, batch, "2");

		CreateStockConsumptionRequest request = new CreateStockConsumptionRequest();
		request.setInventoryItemCode(item.getCode());
		request.setWarehouseCode(warehouse.getCode());
		request.setWarehouseZoneCode(zone.getCode());
		request.setQuantity(new BigDecimal("2"));
		request.setReferenceType("surgery");
		request.setReferenceId("surgery-1");

		when(inventoryItemRepository.findByCodeIgnoreCase(item.getCode())).thenReturn(Optional.of(item));
		when(warehouseRepository.findByCodeIgnoreCase(warehouse.getCode())).thenReturn(Optional.of(warehouse));
		when(warehouseZoneRepository.findByWarehouseIdAndCodeIgnoreCase(warehouse.getId(), zone.getCode()))
				.thenReturn(Optional.of(zone));
		when(stockBatchRepository.findAllByItemAndLocationOrderByExpiry(item.getId(), warehouse.getId(), zone.getId()))
				.thenReturn(List.of(batch));
		when(stockReservationRepository.findAllByReferenceTypeAndReferenceIdAndStatus(
				"surgery",
				"surgery-1",
				ReservationStatus.ACTIVE)).thenReturn(List.of(reservation));
		when(stockReservationRepository.sumQuantityByBatchIdAndStatus(batch.getId(), ReservationStatus.ACTIVE))
				.thenReturn(BigDecimal.ZERO);

		var response = stockConsumptionService.consume(request);

		assertEquals(new BigDecimal("2"), response.getConsumedQuantity());
		assertEquals(1, response.getLines().size());
		assertEquals(batch.getId(), response.getLines().getFirst().getStockBatchId());

		ArgumentCaptor<StockReservation> reservationCaptor = ArgumentCaptor.forClass(StockReservation.class);
		verify(stockReservationRepository).save(reservationCaptor.capture());
		assertEquals(ReservationStatus.CONSUMED, reservationCaptor.getValue().getStatus());
		verify(stockBatchRepository).save(any(StockBatch.class));
		verify(stockMovementRepository).save(any());
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

	private StockReservation reservation(
			InventoryItem item,
			Warehouse warehouse,
			WarehouseZone zone,
			StockBatch batch,
			String quantity) {
		StockReservation reservation = new StockReservation();
		reservation.setId(UUID.randomUUID());
		reservation.setInventoryItem(item);
		reservation.setWarehouse(warehouse);
		reservation.setWarehouseZone(zone);
		reservation.setStockBatch(batch);
		reservation.setQuantity(new BigDecimal(quantity));
		reservation.setStatus(ReservationStatus.ACTIVE);
		return reservation;
	}
}
