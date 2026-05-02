package com.hospital.inventory.supplier.controller;

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
import com.hospital.inventory.supplier.dto.CreateSupplierRequest;
import com.hospital.inventory.supplier.dto.SupplierResponse;
import com.hospital.inventory.supplier.dto.UpdateSupplierRequest;
import com.hospital.inventory.supplier.service.SupplierService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory/suppliers")
@RequirePermission(PermissionCodes.INVENTORY_SUPPLIERS_READ)
public class SupplierController {

	private final SupplierService supplierService;

	public SupplierController(SupplierService supplierService) {
		this.supplierService = supplierService;
	}

	@PostMapping
	@RequirePermission(PermissionCodes.INVENTORY_SUPPLIERS_WRITE)
	public ApiResponse<SupplierResponse> create(@Valid @RequestBody CreateSupplierRequest request) {
		return ApiResponse.success("Supplier created successfully", supplierService.create(request));
	}

	@PutMapping("/{id}")
	@RequirePermission(PermissionCodes.INVENTORY_SUPPLIERS_WRITE)
	public ApiResponse<SupplierResponse> update(
			@PathVariable UUID id,
			@Valid @RequestBody UpdateSupplierRequest request) {
		return ApiResponse.success("Supplier updated successfully", supplierService.update(id, request));
	}

	@GetMapping("/{id}")
	public ApiResponse<SupplierResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Supplier retrieved successfully", supplierService.getById(id));
	}

	@GetMapping
	public ApiResponse<PageResponse<SupplierResponse>> getAll(
			@RequestParam(required = false) String search,
			@PageableDefault(size = 20) Pageable pageable) {
		if (search != null && !search.isBlank()) {
			return ApiResponse.success(
					"Suppliers searched successfully",
					PageResponse.from(supplierService.search(search, pageable)));
		}
		return ApiResponse.success("Suppliers retrieved successfully", PageResponse.from(supplierService.getAll(pageable)));
	}
}
