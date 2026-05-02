package com.hospital.inventory.stock.controller;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.stock.dto.CreateStockReservationRequest;
import com.hospital.inventory.stock.dto.StockReservationResponse;
import com.hospital.inventory.stock.service.StockReservationService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/reservations")
public class StockReservationController {

	private final StockReservationService stockReservationService;

	public StockReservationController(StockReservationService stockReservationService) {
		this.stockReservationService = stockReservationService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_RESERVE)
	public ApiResponse<StockReservationResponse> create(@Valid @RequestBody CreateStockReservationRequest request) {
		return ApiResponse.success("Stock reservation created successfully", stockReservationService.create(request));
	}

	@PostMapping("/{reservationId}/release")
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_RESERVE)
	public ApiResponse<StockReservationResponse> release(@PathVariable UUID reservationId) {
		return ApiResponse.success("Stock reservation released successfully",
				stockReservationService.release(reservationId));
	}
}
