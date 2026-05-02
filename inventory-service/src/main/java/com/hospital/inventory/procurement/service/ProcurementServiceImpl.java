package com.hospital.inventory.procurement.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.DuplicateResourceException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.procurement.dto.CreatePurchaseOrderRequest;
import com.hospital.inventory.procurement.dto.CreateSupplierCatalogItemRequest;
import com.hospital.inventory.procurement.dto.PurchaseOrderItemRequest;
import com.hospital.inventory.procurement.dto.PurchaseOrderResponse;
import com.hospital.inventory.procurement.dto.SupplierCatalogItemResponse;
import com.hospital.inventory.procurement.model.PurchaseOrder;
import com.hospital.inventory.procurement.model.PurchaseOrderItem;
import com.hospital.inventory.procurement.model.PurchaseOrderStatus;
import com.hospital.inventory.procurement.model.SupplierCatalogItem;
import com.hospital.inventory.procurement.repository.PurchaseOrderRepository;
import com.hospital.inventory.procurement.repository.SupplierCatalogItemRepository;
import com.hospital.inventory.supplier.model.Supplier;
import com.hospital.inventory.supplier.repository.SupplierRepository;

@Service
public class ProcurementServiceImpl implements ProcurementService {

	private final SupplierRepository supplierRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final SupplierCatalogItemRepository supplierCatalogItemRepository;
	private final PurchaseOrderRepository purchaseOrderRepository;

	public ProcurementServiceImpl(
			SupplierRepository supplierRepository,
			InventoryItemRepository inventoryItemRepository,
			SupplierCatalogItemRepository supplierCatalogItemRepository,
			PurchaseOrderRepository purchaseOrderRepository) {
		this.supplierRepository = supplierRepository;
		this.inventoryItemRepository = inventoryItemRepository;
		this.supplierCatalogItemRepository = supplierCatalogItemRepository;
		this.purchaseOrderRepository = purchaseOrderRepository;
	}

	@Override
	@Transactional
	public SupplierCatalogItemResponse createSupplierCatalogItem(UUID supplierId, CreateSupplierCatalogItemRequest request) {
		Supplier supplier = getSupplier(supplierId);
		InventoryItem item = getInventoryItem(request.getInventoryItemId());
		if (supplierCatalogItemRepository.existsBySupplierIdAndInventoryItemIdAndUnitCodeIgnoreCase(
				supplierId,
				item.getId(),
				request.getUnitCode().trim())) {
			throw new DuplicateResourceException("Supplier catalog item already exists for supplier, item and unit");
		}

		SupplierCatalogItem catalogItem = new SupplierCatalogItem();
		catalogItem.setSupplier(supplier);
		catalogItem.setInventoryItem(item);
		catalogItem.setSupplierItemCode(request.getSupplierItemCode().trim());
		catalogItem.setUnitCode(request.getUnitCode().trim());
		catalogItem.setUnitPrice(request.getUnitPrice());
		catalogItem.setActive(request.isActive());
		return toResponse(supplierCatalogItemRepository.save(catalogItem));
	}

	@Override
	@Transactional(readOnly = true)
	public List<SupplierCatalogItemResponse> getSupplierCatalogItems(UUID supplierId) {
		getSupplier(supplierId);
		return supplierCatalogItemRepository.findAllBySupplierId(supplierId).stream().map(this::toResponse).toList();
	}

	@Override
	@Transactional
	public PurchaseOrderResponse createPurchaseOrder(CreatePurchaseOrderRequest request) {
		if (purchaseOrderRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
			throw new DuplicateResourceException("Purchase order code already exists: " + request.getCode());
		}

		Supplier supplier = getSupplier(request.getSupplierId());
		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setSupplier(supplier);
		purchaseOrder.setCode(request.getCode().trim());
		purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);
		purchaseOrder.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);
		syncItems(purchaseOrder, request.getItems());
		return toResponse(purchaseOrderRepository.save(purchaseOrder));
	}

	@Override
	@Transactional(readOnly = true)
	public PurchaseOrderResponse getPurchaseOrderById(UUID id) {
		return toResponse(getPurchaseOrder(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<PurchaseOrderResponse> getPurchaseOrders(Pageable pageable) {
		return purchaseOrderRepository.findAll(pageable).map(this::toResponse);
	}

	private Supplier getSupplier(UUID id) {
		return supplierRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + id));
	}

	private InventoryItem getInventoryItem(UUID id) {
		return inventoryItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + id));
	}

	private PurchaseOrder getPurchaseOrder(UUID id) {
		return purchaseOrderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Purchase order not found: " + id));
	}

	private void syncItems(PurchaseOrder purchaseOrder, List<PurchaseOrderItemRequest> items) {
		purchaseOrder.getItems().clear();
		for (PurchaseOrderItemRequest itemRequest : items) {
			PurchaseOrderItem item = new PurchaseOrderItem();
			item.setPurchaseOrder(purchaseOrder);
			item.setInventoryItem(getInventoryItem(itemRequest.getInventoryItemId()));
			item.setSupplierCatalogItem(itemRequest.getSupplierCatalogItemId() != null
					? supplierCatalogItemRepository.findById(itemRequest.getSupplierCatalogItemId())
							.orElseThrow(() -> new ResourceNotFoundException(
									"Supplier catalog item not found: " + itemRequest.getSupplierCatalogItemId()))
					: null);
			item.setUnitCode(itemRequest.getUnitCode() != null ? itemRequest.getUnitCode().trim() : null);
			item.setQuantity(itemRequest.getQuantity());
			item.setUnitPrice(itemRequest.getUnitPrice());
			item.setReceivedQuantity(BigDecimal.ZERO);
			purchaseOrder.getItems().add(item);
		}
	}

	private SupplierCatalogItemResponse toResponse(SupplierCatalogItem catalogItem) {
		SupplierCatalogItemResponse response = new SupplierCatalogItemResponse();
		response.setId(catalogItem.getId());
		response.setSupplierId(catalogItem.getSupplier().getId());
		response.setInventoryItemId(catalogItem.getInventoryItem().getId());
		response.setSupplierItemCode(catalogItem.getSupplierItemCode());
		response.setUnitCode(catalogItem.getUnitCode());
		response.setUnitPrice(catalogItem.getUnitPrice());
		response.setActive(catalogItem.isActive());
		response.setCreatedAt(catalogItem.getCreatedAt());
		response.setUpdatedAt(catalogItem.getUpdatedAt());
		return response;
	}

	private PurchaseOrderResponse toResponse(PurchaseOrder purchaseOrder) {
		PurchaseOrderResponse response = new PurchaseOrderResponse();
		response.setId(purchaseOrder.getId());
		response.setSupplierId(purchaseOrder.getSupplier().getId());
		response.setCode(purchaseOrder.getCode());
		response.setStatus(purchaseOrder.getStatus().name());
		response.setNotes(purchaseOrder.getNotes());
		response.setCreatedAt(purchaseOrder.getCreatedAt());
		response.setUpdatedAt(purchaseOrder.getUpdatedAt());
		response.setItems(purchaseOrder.getItems().stream().map(this::toResponse).toList());
		return response;
	}

	private PurchaseOrderResponse.Item toResponse(PurchaseOrderItem item) {
		PurchaseOrderResponse.Item response = new PurchaseOrderResponse.Item();
		response.setId(item.getId());
		response.setInventoryItemId(item.getInventoryItem().getId());
		response.setSupplierCatalogItemId(item.getSupplierCatalogItem() != null ? item.getSupplierCatalogItem().getId() : null);
		response.setUnitCode(item.getUnitCode());
		response.setQuantity(item.getQuantity());
		response.setUnitPrice(item.getUnitPrice());
		response.setReceivedQuantity(item.getReceivedQuantity());
		return response;
	}
}
