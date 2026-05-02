package com.hospital.inventory.stock.controller;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.stock.dto.CreateSurgeryStockReservationRequest;
import com.hospital.inventory.stock.dto.SurgeryStockReservationResponse;
import com.hospital.inventory.stock.service.SurgeryStockReservationService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory")
public class SurgeryStockReservationController {

	private final SurgeryStockReservationService surgeryStockReservationService;

	public SurgeryStockReservationController(SurgeryStockReservationService surgeryStockReservationService) {
		this.surgeryStockReservationService = surgeryStockReservationService;
	}

	@PostMapping("/reservations/surgeries")
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_RESERVE)
	public ApiResponse<SurgeryStockReservationResponse> createReservations(
			@Valid @RequestBody CreateSurgeryStockReservationRequest request) {
		return ApiResponse.success(
				"Surgery stock reservations created successfully",
				surgeryStockReservationService.createReservations(request));
	}

	@PostMapping("/surgeries/{surgeryId}/release")
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_RESERVE)
	public ApiResponse<SurgeryStockReservationResponse> releaseReservations(@PathVariable UUID surgeryId) {
		return ApiResponse.success(
				"Surgery stock reservations released successfully",
				surgeryStockReservationService.releaseReservations(surgeryId));
	}

	@GetMapping("/surgeries/{surgeryId}/reservation-status")
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_READ)
	public ApiResponse<SurgeryStockReservationResponse> getReservationStatus(@PathVariable UUID surgeryId) {
		return ApiResponse.success(
				"Surgery stock reservation status retrieved successfully",
				surgeryStockReservationService.getReservationStatus(surgeryId));
	}
}
