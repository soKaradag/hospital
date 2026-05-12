package com.hospital.inventory.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.warehouse.mapper.WarehouseMapper;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@ExtendWith(MockitoExtension.class)
class WarehouseServiceImplTest {

	@Mock
	private WarehouseRepository warehouseRepository;

	@Mock
	private WarehouseZoneRepository warehouseZoneRepository;

	@Mock
	private StockBatchRepository stockBatchRepository;

	@Mock
	private WarehouseMapper warehouseMapper;

	@InjectMocks
	private WarehouseServiceImpl warehouseService;

	@Test
	void deleteShouldDeactivateWarehouseWhenNoActiveStockBatchesExist() {
		UUID warehouseId = UUID.randomUUID();
		Warehouse warehouse = warehouse(warehouseId);
		when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
		when(stockBatchRepository.countByWarehouse_IdAndActiveTrue(warehouseId)).thenReturn(0L);

		warehouseService.delete(warehouseId);

		assertFalse(warehouse.isActive());
		verify(warehouseRepository).save(warehouse);
	}

	@Test
	void deleteShouldRejectWarehouseWithActiveStockBatches() {
		UUID warehouseId = UUID.randomUUID();
		Warehouse warehouse = warehouse(warehouseId);
		when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
		when(stockBatchRepository.countByWarehouse_IdAndActiveTrue(warehouseId)).thenReturn(1L);

		BusinessRuleViolationException exception = assertThrows(
				BusinessRuleViolationException.class,
				() -> warehouseService.delete(warehouseId));

		assertEquals("Warehouse with active stock batches cannot be deleted", exception.getMessage());
	}

	@Test
	void deleteZoneShouldDeactivateZoneWhenNoActiveStockBatchesExist() {
		UUID warehouseId = UUID.randomUUID();
		UUID zoneId = UUID.randomUUID();
		Warehouse warehouse = warehouse(warehouseId);
		WarehouseZone zone = zone(zoneId, warehouse);
		when(warehouseZoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
		when(stockBatchRepository.countByWarehouseZone_IdAndActiveTrue(zoneId)).thenReturn(0L);

		warehouseService.deleteZone(warehouseId, zoneId);

		assertFalse(zone.isActive());
		verify(warehouseZoneRepository).save(zone);
	}

	@Test
	void deleteZoneShouldRejectZoneWithActiveStockBatches() {
		UUID warehouseId = UUID.randomUUID();
		UUID zoneId = UUID.randomUUID();
		Warehouse warehouse = warehouse(warehouseId);
		WarehouseZone zone = zone(zoneId, warehouse);
		when(warehouseZoneRepository.findById(zoneId)).thenReturn(Optional.of(zone));
		when(stockBatchRepository.countByWarehouseZone_IdAndActiveTrue(zoneId)).thenReturn(1L);

		BusinessRuleViolationException exception = assertThrows(
				BusinessRuleViolationException.class,
				() -> warehouseService.deleteZone(warehouseId, zoneId));

		assertEquals("Warehouse zone with active stock batches cannot be deleted", exception.getMessage());
	}

	private Warehouse warehouse(UUID id) {
		Warehouse warehouse = new Warehouse();
		warehouse.setId(id);
		warehouse.setActive(true);
		return warehouse;
	}

	private WarehouseZone zone(UUID id, Warehouse warehouse) {
		WarehouseZone zone = new WarehouseZone();
		zone.setId(id);
		zone.setWarehouse(warehouse);
		zone.setActive(true);
		return zone;
	}
}
