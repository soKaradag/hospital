package com.hospital.inventory.inventorycategory.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.inventorycategory.mapper.InventoryCategoryMapper;
import com.hospital.inventory.inventorycategory.model.InventoryCategory;
import com.hospital.inventory.inventorycategory.repository.InventoryCategoryRepository;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;

@ExtendWith(MockitoExtension.class)
class InventoryCategoryServiceImplTest {

	@Mock
	private InventoryCategoryRepository inventoryCategoryRepository;

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private InventoryCategoryMapper inventoryCategoryMapper;

	@InjectMocks
	private InventoryCategoryServiceImpl inventoryCategoryService;

	@Test
	void deleteShouldDeactivateCategoryWhenNoActiveItemsReferenceIt() {
		UUID id = UUID.randomUUID();
		InventoryCategory category = new InventoryCategory();
		category.setId(id);
		category.setActive(true);

		when(inventoryCategoryRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(category));
		when(inventoryItemRepository.countByCategoryIdAndActiveTrue(id)).thenReturn(0L);

		inventoryCategoryService.delete(id);

		verify(inventoryCategoryRepository).save(category);
	}

	@Test
	void deleteShouldRejectCategoryWhenActiveItemsStillReferenceIt() {
		UUID id = UUID.randomUUID();
		InventoryCategory category = new InventoryCategory();
		category.setId(id);
		category.setActive(true);

		when(inventoryCategoryRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(category));
		when(inventoryItemRepository.countByCategoryIdAndActiveTrue(id)).thenReturn(2L);

		assertThrows(BusinessRuleViolationException.class, () -> inventoryCategoryService.delete(id));
		verify(inventoryCategoryRepository, never()).save(category);
	}
}
