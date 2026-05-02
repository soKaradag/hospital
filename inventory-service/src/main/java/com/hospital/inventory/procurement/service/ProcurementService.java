package com.hospital.inventory.procurement.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.inventory.procurement.dto.CreateGoodsReceiptRequest;
import com.hospital.inventory.procurement.dto.CreatePurchaseOrderRequest;
import com.hospital.inventory.procurement.dto.CreateSupplierCatalogItemRequest;
import com.hospital.inventory.procurement.dto.GoodsReceiptResponse;
import com.hospital.inventory.procurement.dto.PurchaseOrderResponse;
import com.hospital.inventory.procurement.dto.SupplierCatalogItemResponse;

public interface ProcurementService {

	SupplierCatalogItemResponse createSupplierCatalogItem(UUID supplierId, CreateSupplierCatalogItemRequest request);

	List<SupplierCatalogItemResponse> getSupplierCatalogItems(UUID supplierId);

	PurchaseOrderResponse createPurchaseOrder(CreatePurchaseOrderRequest request);

	PurchaseOrderResponse approvePurchaseOrder(UUID id);

	PurchaseOrderResponse cancelPurchaseOrder(UUID id);

	PurchaseOrderResponse getPurchaseOrderById(UUID id);

	Page<PurchaseOrderResponse> getPurchaseOrders(Pageable pageable);

	GoodsReceiptResponse createGoodsReceipt(CreateGoodsReceiptRequest request);
}
