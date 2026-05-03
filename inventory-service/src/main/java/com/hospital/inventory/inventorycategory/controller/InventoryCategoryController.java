package com.hospital.inventory.inventorycategory.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.hospital.inventory.inventorycategory.dto.CreateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.dto.InventoryCategoryResponse;
import com.hospital.inventory.inventorycategory.dto.UpdateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.service.InventoryCategoryService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/categories")
@RequirePermission(PermissionCodes.INVENTORY_ITEMS_READ)
public class InventoryCategoryController {

	private final InventoryCategoryService inventoryCategoryService;

	public InventoryCategoryController(InventoryCategoryService inventoryCategoryService) {
		this.inventoryCategoryService = inventoryCategoryService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_ITEMS_WRITE)
	public ApiResponse<InventoryCategoryResponse> create(@Valid @RequestBody CreateInventoryCategoryRequest request) {
		return ApiResponse.success(
				"Inventory category created successfully",
				inventoryCategoryService.create(request));
	}

	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.INVENTORY_ITEMS_WRITE)
	public ApiResponse<InventoryCategoryResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateInventoryCategoryRequest request) {
		return ApiResponse.success(
				"Inventory category updated successfully",
				inventoryCategoryService.update(id, request));
	}

	@GetMapping("/{id}")
	public ApiResponse<InventoryCategoryResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success(
				"Inventory category retrieved successfully",
				inventoryCategoryService.getById(id));
	}

	@GetMapping
	public ApiResponse<PageResponse<InventoryCategoryResponse>> getAll(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success(
					"Inventory categories searched successfully",
					PageResponse.from(inventoryCategoryService.search(search, pageable)));
		}
		return ApiResponse.success(
				"Inventory categories retrieved successfully",
				PageResponse.from(inventoryCategoryService.getAll(pageable)));
	}

	@DeleteMapping("/{id}")
	@RequirePermission(PermissionCodes.INVENTORY_ITEMS_WRITE)
	public ApiResponse<Void> delete(@PathVariable UUID id) {
		inventoryCategoryService.delete(id);
		return ApiResponse.success("Inventory category deleted successfully", null);
	}
}
