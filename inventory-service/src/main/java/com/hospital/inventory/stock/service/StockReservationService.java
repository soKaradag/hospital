package com.hospital.inventory.stock.service;

import java.util.UUID;

import com.hospital.inventory.stock.dto.CreateStockReservationRequest;
import com.hospital.inventory.stock.dto.StockReservationResponse;

public interface StockReservationService {

	StockReservationResponse create(CreateStockReservationRequest request);

	StockReservationResponse release(UUID reservationId);
}
