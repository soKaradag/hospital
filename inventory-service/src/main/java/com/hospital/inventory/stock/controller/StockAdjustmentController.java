package com.hospital.inventory.stock.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.stock.dto.CreateStockAdjustmentRequest;
import com.hospital.inventory.stock.dto.StockAdjustmentResponse;
import com.hospital.inventory.stock.service.StockAdjustmentService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/adjustments")
public class StockAdjustmentController {

	private final StockAdjustmentService stockAdjustmentService;

	public StockAdjustmentController(StockAdjustmentService stockAdjustmentService) {
		this.stockAdjustmentService = stockAdjustmentService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_ADJUST)
	public ApiResponse<StockAdjustmentResponse> create(@Valid @RequestBody CreateStockAdjustmentRequest request) {
		return ApiResponse.success("Stock adjustment created successfully", stockAdjustmentService.create(request));
	}
}
