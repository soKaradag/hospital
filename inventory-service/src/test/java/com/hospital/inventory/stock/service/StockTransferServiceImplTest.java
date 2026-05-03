package com.hospital.inventory.stock.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import com.hospital.inventory.stock.dto.CreateStockTransferRequest;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.stock.repository.StockTransferRepository;
import com.hospital.inventory.stock.repository.StockTransferRequestRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@ExtendWith(MockitoExtension.class)
class StockTransferServiceImplTest {

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private StockBatchRepository stockBatchRepository;

	@Mock
	private StockMovementRepository stockMovementRepository;

	@Mock
	private StockTransferRequestRepository stockTransferRequestRepository;

	@Mock
	private StockTransferRepository stockTransferRepository;

	@Mock
	private WarehouseRepository warehouseRepository;

	@Mock
	private WarehouseZoneRepository warehouseZoneRepository;

	private StockTransferServiceImpl stockTransferService;

	@BeforeEach
	void setUp() {
		stockTransferService = new StockTransferServiceImpl(
				inventoryItemRepository,
				stockBatchRepository,
				stockMovementRepository,
				stockTransferRequestRepository,
				stockTransferRepository,
				warehouseRepository,
				warehouseZoneRepository);
	}

	@Test
	void createShouldRejectWhenSourceBatchDoesNotBelongToRequestedWarehouse() {
		InventoryItem item = item();
		Warehouse requestedWarehouse = warehouse();
		Warehouse actualWarehouse = warehouse();
		Warehouse destinationWarehouse = warehouse();
		StockBatch batch = batch(item, actualWarehouse, null);

		CreateStockTransferRequest request = request(item.getId(), batch.getId(), requestedWarehouse.getId(), destinationWarehouse.getId());

		when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(stockBatchRepository.findById(batch.getId())).thenReturn(Optional.of(batch));
		when(warehouseRepository.findById(requestedWarehouse.getId())).thenReturn(Optional.of(requestedWarehouse));
		when(warehouseRepository.findById(destinationWarehouse.getId())).thenReturn(Optional.of(destinationWarehouse));

		assertThrows(BusinessRuleViolationException.class, () -> stockTransferService.create(request));
		verify(stockTransferRequestRepository, never()).save(org.mockito.ArgumentMatchers.any());
	}

	@Test
	void createShouldRejectWhenDestinationZoneDoesNotBelongToDestinationWarehouse() {
		InventoryItem item = item();
		Warehouse fromWarehouse = warehouse();
		Warehouse toWarehouse = warehouse();
		WarehouseZone foreignZone = zone(warehouse());
		StockBatch batch = batch(item, fromWarehouse, null);

		CreateStockTransferRequest request = request(item.getId(), batch.getId(), fromWarehouse.getId(), toWarehouse.getId());
		request.setToWarehouseZoneId(foreignZone.getId());

		when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(stockBatchRepository.findById(batch.getId())).thenReturn(Optional.of(batch));
		when(warehouseRepository.findById(fromWarehouse.getId())).thenReturn(Optional.of(fromWarehouse));
		when(warehouseRepository.findById(toWarehouse.getId())).thenReturn(Optional.of(toWarehouse));
		when(warehouseZoneRepository.findById(foreignZone.getId())).thenReturn(Optional.of(foreignZone));

		assertThrows(BusinessRuleViolationException.class, () -> stockTransferService.create(request));
		verify(stockTransferRequestRepository, never()).save(org.mockito.ArgumentMatchers.any());
	}

	private CreateStockTransferRequest request(UUID itemId, UUID batchId, UUID fromWarehouseId, UUID toWarehouseId) {
		CreateStockTransferRequest request = new CreateStockTransferRequest();
		request.setItemId(itemId);
		request.setBatchId(batchId);
		request.setFromWarehouseId(fromWarehouseId);
		request.setToWarehouseId(toWarehouseId);
		request.setQuantity(new BigDecimal("1"));
		return request;
	}

	private InventoryItem item() {
		InventoryItem item = new InventoryItem();
		item.setId(UUID.randomUUID());
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

	private StockBatch batch(InventoryItem item, Warehouse warehouse, WarehouseZone zone) {
		StockBatch batch = new StockBatch();
		batch.setId(UUID.randomUUID());
		batch.setInventoryItem(item);
		batch.setWarehouse(warehouse);
		batch.setWarehouseZone(zone);
		batch.setBatchNumber("LOT-1");
		batch.setQuantityOnHand(new BigDecimal("5"));
		batch.setActive(true);
		return batch;
	}
}
