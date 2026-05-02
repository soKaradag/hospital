package com.hospital.inventory.procurement.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import com.hospital.inventory.common.dto.PageResponse;
import com.hospital.inventory.procurement.dto.CreateGoodsReceiptRequest;
import com.hospital.inventory.procurement.dto.CreatePurchaseOrderRequest;
import com.hospital.inventory.procurement.dto.CreateSupplierCatalogItemRequest;
import com.hospital.inventory.procurement.dto.GoodsReceiptResponse;
import com.hospital.inventory.procurement.dto.PurchaseOrderResponse;
import com.hospital.inventory.procurement.dto.SupplierCatalogItemResponse;
import com.hospital.inventory.procurement.service.ProcurementService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory")
public class ProcurementController {

	private final ProcurementService procurementService;

	public ProcurementController(ProcurementService procurementService) {
		this.procurementService = procurementService;
	}

	@PostMapping("/suppliers/{supplierId}/catalog-items")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_WRITE)
	public ApiResponse<SupplierCatalogItemResponse> createSupplierCatalogItem(
			@PathVariable UUID supplierId,
			@Valid @RequestBody CreateSupplierCatalogItemRequest request) {
		return ApiResponse.success(
				"Supplier catalog item created successfully",
				procurementService.createSupplierCatalogItem(supplierId, request));
	}

	@GetMapping("/suppliers/{supplierId}/catalog-items")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_READ)
	public ApiResponse<List<SupplierCatalogItemResponse>> getSupplierCatalogItems(@PathVariable UUID supplierId) {
		return ApiResponse.success(
				"Supplier catalog items retrieved successfully",
				procurementService.getSupplierCatalogItems(supplierId));
	}

	@PostMapping("/purchase-orders")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_WRITE)
	public ApiResponse<PurchaseOrderResponse> createPurchaseOrder(
			@Valid @RequestBody CreatePurchaseOrderRequest request) {
		return ApiResponse.success(
				"Purchase order created successfully",
				procurementService.createPurchaseOrder(request));
	}

	@PostMapping("/purchase-orders/{id}/approve")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_WRITE)
	public ApiResponse<PurchaseOrderResponse> approvePurchaseOrder(@PathVariable UUID id) {
		return ApiResponse.success(
				"Purchase order approved successfully",
				procurementService.approvePurchaseOrder(id));
	}

	@PostMapping("/purchase-orders/{id}/cancel")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_WRITE)
	public ApiResponse<PurchaseOrderResponse> cancelPurchaseOrder(@PathVariable UUID id) {
		return ApiResponse.success(
				"Purchase order cancelled successfully",
				procurementService.cancelPurchaseOrder(id));
	}

	@GetMapping("/purchase-orders/{id}")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_READ)
	public ApiResponse<PurchaseOrderResponse> getPurchaseOrderById(@PathVariable UUID id) {
		return ApiResponse.success(
				"Purchase order retrieved successfully",
				procurementService.getPurchaseOrderById(id));
	}

	@GetMapping("/purchase-orders")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_READ)
	public ApiResponse<PageResponse<PurchaseOrderResponse>> getPurchaseOrders(
			@PageableDefault(size = 20) Pageable pageable) {
		return ApiResponse.success(
				"Purchase orders retrieved successfully",
				PageResponse.from(procurementService.getPurchaseOrders(pageable)));
	}

	@PostMapping("/receipts")
	@RequirePermission(PermissionCodes.INVENTORY_RECEIPTS_WRITE)
	public ApiResponse<GoodsReceiptResponse> createGoodsReceipt(@Valid @RequestBody CreateGoodsReceiptRequest request) {
		return ApiResponse.success(
				"Goods receipt created successfully",
				procurementService.createGoodsReceipt(request));
	}
}
