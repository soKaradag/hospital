package com.hospital.inventory.stock.service;

import java.util.UUID;

import com.hospital.inventory.stock.dto.CreateSurgeryStockReservationRequest;
import com.hospital.inventory.stock.dto.SurgeryStockReservationResponse;

public interface SurgeryStockReservationService {

	SurgeryStockReservationResponse createReservations(CreateSurgeryStockReservationRequest request);

	SurgeryStockReservationResponse releaseReservations(UUID surgeryId);

	SurgeryStockReservationResponse getReservationStatus(UUID surgeryId);
}
