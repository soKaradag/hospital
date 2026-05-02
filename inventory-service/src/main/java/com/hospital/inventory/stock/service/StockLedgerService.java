package com.hospital.inventory.stock.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.inventory.stock.dto.StockAvailabilityResponse;
import com.hospital.inventory.stock.dto.StockMovementResponse;

public interface StockLedgerService {

	StockAvailabilityResponse getAvailability(UUID itemId);

	Page<StockMovementResponse> getMovements(UUID itemId, Pageable pageable);
}
