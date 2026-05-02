package com.hospital.inventory.stock.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.stock.dto.CreateStockConsumptionRequest;
import com.hospital.inventory.stock.dto.StockConsumptionResponse;
import com.hospital.inventory.stock.service.StockConsumptionService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/consumptions")
public class StockConsumptionController {

	private final StockConsumptionService stockConsumptionService;

	public StockConsumptionController(StockConsumptionService stockConsumptionService) {
		this.stockConsumptionService = stockConsumptionService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_CONSUME)
	public ApiResponse<StockConsumptionResponse> consume(@Valid @RequestBody CreateStockConsumptionRequest request) {
		return ApiResponse.success(
				"Stock consumed successfully",
				stockConsumptionService.consume(request));
	}
}
