package com.hospital.inventory.stock.service;

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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockCountRequest;
import com.hospital.inventory.stock.dto.StockCountLineRequest;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.repository.StockAdjustmentRepository;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockCountLineRepository;
import com.hospital.inventory.stock.repository.StockCountRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@ExtendWith(MockitoExtension.class)
class StockCountServiceImplTest {

	@Mock
	private WarehouseRepository warehouseRepository;

	@Mock
	private WarehouseZoneRepository warehouseZoneRepository;

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private StockBatchRepository stockBatchRepository;

	@Mock
	private StockCountRepository stockCountRepository;

	@Mock
	private StockCountLineRepository stockCountLineRepository;

	@Mock
	private StockAdjustmentRepository stockAdjustmentRepository;

	@Mock
	private StockMovementRepository stockMovementRepository;

	private StockCountServiceImpl stockCountService;

	@BeforeEach
	void setUp() {
		stockCountService = new StockCountServiceImpl(
				warehouseRepository,
				warehouseZoneRepository,
				inventoryItemRepository,
				stockBatchRepository,
				stockCountRepository,
				stockCountLineRepository,
				stockAdjustmentRepository,
				stockMovementRepository);
	}

	@Test
	void createShouldRejectWhenBatchDoesNotBelongToRequestedLocation() {
		InventoryItem item = item();
		Warehouse selectedWarehouse = warehouse();
		Warehouse foreignWarehouse = warehouse();
		StockBatch batch = batch(item, foreignWarehouse, null);

		CreateStockCountRequest request = request(selectedWarehouse.getId(), null, line(item.getId(), batch.getId()));

		when(warehouseRepository.findById(selectedWarehouse.getId())).thenReturn(Optional.of(selectedWarehouse));
		when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(stockBatchRepository.findById(batch.getId())).thenReturn(Optional.of(batch));
		when(stockCountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		assertThrows(BusinessRuleViolationException.class, () -> stockCountService.create(request));
		verify(stockCountLineRepository, never()).save(org.mockito.ArgumentMatchers.any());
	}

	@Test
	void createShouldRejectDuplicateBatchLinesInSameCount() {
		InventoryItem item = item();
		Warehouse warehouse = warehouse();
		StockBatch batch = batch(item, warehouse, null);

		CreateStockCountRequest request = request(
				warehouse.getId(),
				null,
				line(item.getId(), batch.getId()),
				line(item.getId(), batch.getId()));

		when(warehouseRepository.findById(warehouse.getId())).thenReturn(Optional.of(warehouse));
		when(inventoryItemRepository.findById(item.getId())).thenReturn(Optional.of(item));
		when(stockBatchRepository.findById(batch.getId())).thenReturn(Optional.of(batch));
		when(stockCountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		assertThrows(BusinessRuleViolationException.class, () -> stockCountService.create(request));
		verify(stockAdjustmentRepository, never()).save(org.mockito.ArgumentMatchers.any());
		verify(stockMovementRepository, never()).save(org.mockito.ArgumentMatchers.any());
	}

	private CreateStockCountRequest request(UUID warehouseId, UUID warehouseZoneId, StockCountLineRequest... lines) {
		CreateStockCountRequest request = new CreateStockCountRequest();
		request.setWarehouseId(warehouseId);
		request.setWarehouseZoneId(warehouseZoneId);
		request.setLines(List.of(lines));
		return request;
	}

	private StockCountLineRequest line(UUID itemId, UUID batchId) {
		StockCountLineRequest line = new StockCountLineRequest();
		line.setItemId(itemId);
		line.setBatchId(batchId);
		line.setCountedQuantity(new BigDecimal("1"));
		return line;
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
