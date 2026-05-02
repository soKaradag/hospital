package com.hospital.inventory.inventoryitem.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.common.dto.PageResponse;
import com.hospital.inventory.inventoryitem.dto.CreateInventoryItemRequest;
import com.hospital.inventory.inventoryitem.dto.InventoryItemResponse;
import com.hospital.inventory.inventoryitem.dto.UpdateInventoryItemRequest;
import com.hospital.inventory.inventoryitem.service.InventoryItemService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/items")
@RequirePermission(PermissionCodes.INVENTORY_ITEMS_READ)
public class InventoryItemController {

	private final InventoryItemService inventoryItemService;

	public InventoryItemController(InventoryItemService inventoryItemService) {
		this.inventoryItemService = inventoryItemService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_ITEMS_WRITE)
	public ApiResponse<InventoryItemResponse> create(@Valid @RequestBody CreateInventoryItemRequest request) {
		return ApiResponse.success("Inventory item created successfully", inventoryItemService.create(request));
	}

	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.INVENTORY_ITEMS_WRITE)
	public ApiResponse<InventoryItemResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateInventoryItemRequest request) {
		return ApiResponse.success("Inventory item updated successfully", inventoryItemService.update(id, request));
	}

	@GetMapping("/{id}")
	public ApiResponse<InventoryItemResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Inventory item retrieved successfully", inventoryItemService.getById(id));
	}

	@GetMapping
	public ApiResponse<PageResponse<InventoryItemResponse>> getAll(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success(
					"Inventory items searched successfully",
					PageResponse.from(inventoryItemService.search(search, pageable)));
		}
		return ApiResponse.success(
				"Inventory items retrieved successfully",
				PageResponse.from(inventoryItemService.getAll(pageable)));
	}
}
