package com.hospital.inventory.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.stock.dto.SurgeryStockReservationResponse;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.model.StockReservation;
import com.hospital.inventory.stock.repository.StockReservationRepository;
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

	@InjectMocks
	private SurgeryStockReservationServiceImpl surgeryStockReservationService;

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

	private StockReservation reservation(ReservationStatus status) {
		InventoryItem item = new InventoryItem();
		item.setCode("GENERAL_MED");

		StockReservation reservation = new StockReservation();
		reservation.setId(UUID.randomUUID());
		reservation.setInventoryItem(item);
		reservation.setQuantity(new BigDecimal("2"));
		reservation.setStatus(status);
		return reservation;
	}
}
