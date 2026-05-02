package com.hospital.inventory.stock.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.stock.dto.CreateStockTransferRequest;
import com.hospital.inventory.stock.dto.StockTransferResponse;
import com.hospital.inventory.stock.service.StockTransferService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/transfers")
public class StockTransferController {

	private final StockTransferService stockTransferService;

	public StockTransferController(StockTransferService stockTransferService) {
		this.stockTransferService = stockTransferService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_TRANSFER)
	public ApiResponse<StockTransferResponse> create(@Valid @RequestBody CreateStockTransferRequest request) {
		return ApiResponse.success("Stock transfer completed successfully", stockTransferService.create(request));
	}
}
