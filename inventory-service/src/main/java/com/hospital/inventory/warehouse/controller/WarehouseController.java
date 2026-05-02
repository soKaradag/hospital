package com.hospital.inventory.warehouse.controller;

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
import com.hospital.inventory.warehouse.dto.CreateWarehouseRequest;
import com.hospital.inventory.warehouse.dto.CreateWarehouseZoneRequest;
import com.hospital.inventory.warehouse.dto.UpdateWarehouseRequest;
import com.hospital.inventory.warehouse.dto.UpdateWarehouseZoneRequest;
import com.hospital.inventory.warehouse.dto.WarehouseResponse;
import com.hospital.inventory.warehouse.dto.WarehouseZoneResponse;
import com.hospital.inventory.warehouse.service.WarehouseService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/warehouses")
@RequirePermission(PermissionCodes.INVENTORY_WAREHOUSES_READ)
public class WarehouseController {

	private final WarehouseService warehouseService;

	public WarehouseController(WarehouseService warehouseService) {
		this.warehouseService = warehouseService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_WAREHOUSES_WRITE)
	public ApiResponse<WarehouseResponse> create(@Valid @RequestBody CreateWarehouseRequest request) {
		return ApiResponse.success("Warehouse created successfully", warehouseService.create(request));
	}

	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.INVENTORY_WAREHOUSES_WRITE)
	public ApiResponse<WarehouseResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateWarehouseRequest request) {
		return ApiResponse.success("Warehouse updated successfully", warehouseService.update(id, request));
	}

	@GetMapping("/{id}")
	public ApiResponse<WarehouseResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Warehouse retrieved successfully", warehouseService.getById(id));
	}

	@GetMapping
	public ApiResponse<PageResponse<WarehouseResponse>> getAll(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success(
					"Warehouses searched successfully",
					PageResponse.from(warehouseService.search(search, pageable)));
		}
		return ApiResponse.success("Warehouses retrieved successfully", PageResponse.from(warehouseService.getAll(pageable)));
	}

	@PostMapping("/{warehouseId}/zones")
	@RequirePermission(PermissionCodes.INVENTORY_WAREHOUSES_WRITE)
	public ApiResponse<WarehouseZoneResponse> createZone(
			@PathVariable UUID warehouseId,
			@Valid @RequestBody CreateWarehouseZoneRequest request) {
		return ApiResponse.success("Warehouse zone created successfully", warehouseService.createZone(warehouseId, request));
	}

	@PutMapping("/{warehouseId}/zones/{zoneId}")
	@RequirePermission(PermissionCodes.INVENTORY_WAREHOUSES_WRITE)
	public ApiResponse<WarehouseZoneResponse> updateZone(
			@PathVariable UUID warehouseId,
			@PathVariable UUID zoneId,
			@Valid @RequestBody UpdateWarehouseZoneRequest request) {
		return ApiResponse.success("Warehouse zone updated successfully",
				warehouseService.updateZone(warehouseId, zoneId, request));
	}

	@GetMapping("/{warehouseId}/zones/{zoneId}")
	public ApiResponse<WarehouseZoneResponse> getZoneById(
			@PathVariable UUID warehouseId,
			@PathVariable UUID zoneId) {
		return ApiResponse.success("Warehouse zone retrieved successfully",
				warehouseService.getZoneById(warehouseId, zoneId));
	}

	@GetMapping("/{warehouseId}/zones")
	public ApiResponse<PageResponse<WarehouseZoneResponse>> getZones(
			@PathVariable UUID warehouseId,
			@PageableDefault(size = 20) Pageable pageable) {
		return ApiResponse.success("Warehouse zones retrieved successfully",
				PageResponse.from(warehouseService.getZones(warehouseId, pageable)));
	}
}
