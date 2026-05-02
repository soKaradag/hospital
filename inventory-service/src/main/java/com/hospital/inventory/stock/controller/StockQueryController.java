package com.hospital.inventory.stock.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.common.dto.PageResponse;
import com.hospital.inventory.stock.dto.StockAvailabilityResponse;
import com.hospital.inventory.stock.dto.StockMovementResponse;
import com.hospital.inventory.stock.service.StockLedgerService;

@RestController
@RequestMapping("/api/inventory/items")
public class StockQueryController {

	private final StockLedgerService stockLedgerService;

	public StockQueryController(StockLedgerService stockLedgerService) {
		this.stockLedgerService = stockLedgerService;
	}

	@GetMapping("/{itemId}/availability")
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_READ)
	public ApiResponse<StockAvailabilityResponse> getAvailability(@PathVariable UUID itemId) {
		return ApiResponse.success("Inventory item availability retrieved successfully",
				stockLedgerService.getAvailability(itemId));
	}

	@GetMapping("/{itemId}/movements")
	@RequirePermission(PermissionCodes.INVENTORY_STOCK_READ)
	public ApiResponse<PageResponse<StockMovementResponse>> getMovements(
			@PathVariable UUID itemId,
			@PageableDefault(size = 20) Pageable pageable) {
		return ApiResponse.success("Inventory stock movements retrieved successfully",
				PageResponse.from(stockLedgerService.getMovements(itemId, pageable)));
	}
}
