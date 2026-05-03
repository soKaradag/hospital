package com.hospital.inventory.inventoryitem.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.hospital.inventory.inventorycategory.repository.InventoryCategoryRepository;
import com.hospital.inventory.inventoryitem.mapper.InventoryItemMapper;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemAliasRepository;
import com.hospital.inventory.inventoryitem.repository.InventoryItemBarcodeRepository;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.planning.repository.ReorderRuleRepository;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockReservationRepository;

@ExtendWith(MockitoExtension.class)
class InventoryItemServiceImplTest {

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private InventoryItemAliasRepository inventoryItemAliasRepository;

	@Mock
	private InventoryItemBarcodeRepository inventoryItemBarcodeRepository;

	@Mock
	private InventoryCategoryRepository inventoryCategoryRepository;

	@Mock
	private StockBatchRepository stockBatchRepository;

	@Mock
	private StockReservationRepository stockReservationRepository;

	@Mock
	private ReorderRuleRepository reorderRuleRepository;

	@Mock
	private InventoryItemMapper inventoryItemMapper;

	@InjectMocks
	private InventoryItemServiceImpl inventoryItemService;

	@Test
	void deleteShouldDeactivateItemWhenNoOperationalDependenciesRemain() {
		UUID id = UUID.randomUUID();
		InventoryItem item = new InventoryItem();
		item.setId(id);
		item.setActive(true);

		when(inventoryItemRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(item));
		when(stockBatchRepository.sumQuantityOnHandByItemId(id)).thenReturn(BigDecimal.ZERO);
		when(stockReservationRepository.sumQuantityByItemIdAndStatus(id, ReservationStatus.ACTIVE))
				.thenReturn(BigDecimal.ZERO);
		when(reorderRuleRepository.existsByInventoryItemIdAndActiveTrue(id)).thenReturn(false);

		inventoryItemService.delete(id);

		verify(inventoryItemRepository).save(item);
	}

	@Test
	void deleteShouldRejectItemWhenStockOnHandExists() {
		UUID id = UUID.randomUUID();
		InventoryItem item = new InventoryItem();
		item.setId(id);
		item.setActive(true);

		when(inventoryItemRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(item));
		when(stockBatchRepository.sumQuantityOnHandByItemId(id)).thenReturn(BigDecimal.ONE);

		assertThrows(BusinessRuleViolationException.class, () -> inventoryItemService.delete(id));
		verify(inventoryItemRepository, never()).save(item);
	}
}
