package com.hospital.inventory.stock.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.CreateStockReservationRequest;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.stock.repository.StockReservationRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
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

	@InjectMocks
	private StockReservationServiceImpl stockReservationService;

	@Test
	void createShouldRejectReservationWhenAvailableStockIsInsufficient() {
		UUID itemId = UUID.randomUUID();
		UUID warehouseId = UUID.randomUUID();

		InventoryItem item = new InventoryItem();
		item.setId(itemId);
		Warehouse warehouse = new Warehouse();
		warehouse.setId(warehouseId);

		CreateStockReservationRequest request = new CreateStockReservationRequest();
		request.setItemId(itemId);
		request.setWarehouseId(warehouseId);
		request.setQuantity(new BigDecimal("2"));
		request.setReservationType("SURGERY");

		when(inventoryItemRepository.findById(itemId)).thenReturn(Optional.of(item));
		when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
		when(stockBatchRepository.sumQuantityOnHandByItemId(itemId)).thenReturn(new BigDecimal("5"));
		when(stockReservationRepository.sumQuantityByItemIdAndStatus(itemId, ReservationStatus.ACTIVE))
				.thenReturn(new BigDecimal("4"));

		assertThrows(BusinessRuleViolationException.class, () -> stockReservationService.create(request));

		verify(stockReservationRepository, never()).save(any());
	}
}
