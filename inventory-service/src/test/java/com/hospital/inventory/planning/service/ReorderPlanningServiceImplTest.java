package com.hospital.inventory.planning.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.planning.model.ReorderRule;
import com.hospital.inventory.planning.repository.ReorderRuleRepository;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockReservationRepository;
import com.hospital.inventory.supplier.repository.SupplierRepository;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@ExtendWith(MockitoExtension.class)
class ReorderPlanningServiceImplTest {

	@Mock
	private ReorderRuleRepository reorderRuleRepository;

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private WarehouseRepository warehouseRepository;

	@Mock
	private WarehouseZoneRepository warehouseZoneRepository;

	@Mock
	private SupplierRepository supplierRepository;

	@Mock
	private StockBatchRepository stockBatchRepository;

	@Mock
	private StockReservationRepository stockReservationRepository;

	@InjectMocks
	private ReorderPlanningServiceImpl reorderPlanningService;

	@Test
	void deleteRuleShouldDeactivateRule() {
		UUID id = UUID.randomUUID();
		ReorderRule rule = new ReorderRule();
		rule.setId(id);
		rule.setActive(true);

		when(reorderRuleRepository.findByIdAndActiveTrue(id)).thenReturn(Optional.of(rule));

		reorderPlanningService.deleteRule(id);

		verify(reorderRuleRepository).save(rule);
	}
}
