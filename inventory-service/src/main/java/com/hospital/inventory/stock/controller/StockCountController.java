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
import com.hospital.inventory.stock.dto.CreateStockCountRequest;
import com.hospital.inventory.stock.dto.StockCountResponse;
import com.hospital.inventory.stock.service.StockCountService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/counts")
public class StockCountController {

	private final StockCountService stockCountService;

	public StockCountController(StockCountService stockCountService) {
		this.stockCountService = stockCountService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_COUNTS_MANAGE)
	public ApiResponse<StockCountResponse> create(@Valid @RequestBody CreateStockCountRequest request) {
		return ApiResponse.success("Stock count created successfully", stockCountService.create(request));
	}

	@PostMapping("/{countId}/close")
	@RequirePermission(PermissionCodes.INVENTORY_COUNTS_MANAGE)
	public ApiResponse<StockCountResponse> close(@PathVariable UUID countId) {
		return ApiResponse.success("Stock count closed successfully", stockCountService.close(countId));
	}
}
