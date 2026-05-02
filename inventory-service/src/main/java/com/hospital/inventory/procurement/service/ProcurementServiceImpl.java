package com.hospital.inventory.procurement.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.DuplicateResourceException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.procurement.dto.CreateGoodsReceiptRequest;
import com.hospital.inventory.procurement.dto.CreatePurchaseOrderRequest;
import com.hospital.inventory.procurement.dto.CreateSupplierCatalogItemRequest;
import com.hospital.inventory.procurement.dto.GoodsReceiptItemRequest;
import com.hospital.inventory.procurement.dto.GoodsReceiptResponse;
import com.hospital.inventory.procurement.dto.PurchaseOrderItemRequest;
import com.hospital.inventory.procurement.dto.PurchaseOrderResponse;
import com.hospital.inventory.procurement.dto.SupplierCatalogItemResponse;
import com.hospital.inventory.procurement.model.GoodsReceipt;
import com.hospital.inventory.procurement.model.GoodsReceiptItem;
import com.hospital.inventory.procurement.model.PurchaseOrder;
import com.hospital.inventory.procurement.model.PurchaseOrderItem;
import com.hospital.inventory.procurement.model.PurchaseOrderStatus;
import com.hospital.inventory.procurement.model.SupplierCatalogItem;
import com.hospital.inventory.procurement.repository.GoodsReceiptRepository;
import com.hospital.inventory.procurement.repository.PurchaseOrderRepository;
import com.hospital.inventory.procurement.repository.SupplierCatalogItemRepository;
import com.hospital.inventory.stock.model.MovementType;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockMovement;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.supplier.model.Supplier;
import com.hospital.inventory.supplier.repository.SupplierRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@Service
public class ProcurementServiceImpl implements ProcurementService {

	private final SupplierRepository supplierRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final SupplierCatalogItemRepository supplierCatalogItemRepository;
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final GoodsReceiptRepository goodsReceiptRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockMovementRepository stockMovementRepository;

	public ProcurementServiceImpl(
			SupplierRepository supplierRepository,
			InventoryItemRepository inventoryItemRepository,
			SupplierCatalogItemRepository supplierCatalogItemRepository,
			PurchaseOrderRepository purchaseOrderRepository,
			GoodsReceiptRepository goodsReceiptRepository,
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository,
			StockBatchRepository stockBatchRepository,
			StockMovementRepository stockMovementRepository) {
		this.supplierRepository = supplierRepository;
		this.inventoryItemRepository = inventoryItemRepository;
		this.supplierCatalogItemRepository = supplierCatalogItemRepository;
		this.purchaseOrderRepository = purchaseOrderRepository;
		this.goodsReceiptRepository = goodsReceiptRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockMovementRepository = stockMovementRepository;
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
	@Transactional
	public PurchaseOrderResponse approvePurchaseOrder(UUID id) {
		PurchaseOrder purchaseOrder = getPurchaseOrder(id);
		if (purchaseOrder.getStatus() != PurchaseOrderStatus.DRAFT) {
			throw new BusinessRuleViolationException("Only draft purchase orders can be approved");
		}
		purchaseOrder.setStatus(PurchaseOrderStatus.APPROVED);
		return toResponse(purchaseOrderRepository.save(purchaseOrder));
	}

	@Override
	@Transactional
	public PurchaseOrderResponse cancelPurchaseOrder(UUID id) {
		PurchaseOrder purchaseOrder = getPurchaseOrder(id);
		if (purchaseOrder.getStatus() == PurchaseOrderStatus.COMPLETED) {
			throw new BusinessRuleViolationException("Completed purchase orders cannot be cancelled");
		}
		boolean hasReceivedQuantity = purchaseOrder.getItems().stream()
				.anyMatch(item -> item.getReceivedQuantity().compareTo(BigDecimal.ZERO) > 0);
		if (hasReceivedQuantity) {
			throw new BusinessRuleViolationException("Purchase orders with received items cannot be cancelled");
		}
		purchaseOrder.setStatus(PurchaseOrderStatus.CANCELLED);
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

	@Override
	@Transactional
	public GoodsReceiptResponse createGoodsReceipt(CreateGoodsReceiptRequest request) {
		if (goodsReceiptRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
			throw new DuplicateResourceException("Goods receipt code already exists: " + request.getCode());
		}

		PurchaseOrder purchaseOrder = getPurchaseOrder(request.getPurchaseOrderId());
		if (purchaseOrder.getStatus() != PurchaseOrderStatus.APPROVED
				&& purchaseOrder.getStatus() != PurchaseOrderStatus.PARTIALLY_RECEIVED) {
			throw new BusinessRuleViolationException(
					"Goods receipts can only be created for approved or partially received purchase orders");
		}

		Warehouse warehouse = getWarehouse(request.getWarehouseId());
		WarehouseZone warehouseZone = getWarehouseZone(request.getWarehouseZoneId(), warehouse.getId());

		GoodsReceipt goodsReceipt = new GoodsReceipt();
		goodsReceipt.setPurchaseOrder(purchaseOrder);
		goodsReceipt.setWarehouse(warehouse);
		goodsReceipt.setWarehouseZone(warehouseZone);
		goodsReceipt.setCode(request.getCode().trim());
		goodsReceipt.setNotes(request.getNotes() != null ? request.getNotes().trim() : null);
		goodsReceipt.setReceivedAt(Instant.now());

		for (GoodsReceiptItemRequest itemRequest : request.getItems()) {
			PurchaseOrderItem purchaseOrderItem = resolvePurchaseOrderItem(purchaseOrder, itemRequest.getPurchaseOrderItemId());
			validateReceiptItem(purchaseOrderItem, itemRequest);
			StockBatch batch = getOrCreateBatch(
					purchaseOrderItem.getInventoryItem(),
					warehouse,
					warehouseZone,
					itemRequest.getBatchNumber().trim(),
					itemRequest.getExpiresAt());
			batch.setQuantityOnHand(batch.getQuantityOnHand().add(itemRequest.getQuantity()));
			stockBatchRepository.save(batch);

			purchaseOrderItem.setReceivedQuantity(purchaseOrderItem.getReceivedQuantity().add(itemRequest.getQuantity()));

			GoodsReceiptItem receiptItem = new GoodsReceiptItem();
			receiptItem.setGoodsReceipt(goodsReceipt);
			receiptItem.setPurchaseOrderItem(purchaseOrderItem);
			receiptItem.setInventoryItem(purchaseOrderItem.getInventoryItem());
			receiptItem.setStockBatch(batch);
			receiptItem.setBatchNumber(batch.getBatchNumber());
			receiptItem.setExpiresAt(batch.getExpiresAt());
			receiptItem.setQuantity(itemRequest.getQuantity());
			receiptItem.setUnitPrice(purchaseOrderItem.getUnitPrice());
			goodsReceipt.getItems().add(receiptItem);
		}

		updatePurchaseOrderStatusAfterReceipt(purchaseOrder);
		GoodsReceipt savedReceipt = goodsReceiptRepository.save(goodsReceipt);
		writeInboundMovements(savedReceipt);
		return toGoodsReceiptResponse(savedReceipt);
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

	private Warehouse getWarehouse(UUID id) {
		return warehouseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + id));
	}

	private WarehouseZone getWarehouseZone(UUID id, UUID warehouseId) {
		if (id == null) {
			return null;
		}
		WarehouseZone zone = warehouseZoneRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse zone not found: " + id));
		if (!zone.getWarehouse().getId().equals(warehouseId)) {
			throw new BusinessRuleViolationException("Warehouse zone does not belong to the selected warehouse");
		}
		return zone;
	}

	private void syncItems(PurchaseOrder purchaseOrder, List<PurchaseOrderItemRequest> items) {
		purchaseOrder.getItems().clear();
		for (PurchaseOrderItemRequest itemRequest : items) {
			if (itemRequest.getUnitCode() == null || itemRequest.getUnitCode().isBlank()) {
				throw new BusinessRuleViolationException("unitCode must not be blank");
			}
			PurchaseOrderItem item = new PurchaseOrderItem();
			item.setPurchaseOrder(purchaseOrder);
			InventoryItem inventoryItem = getInventoryItem(itemRequest.getInventoryItemId());
			item.setInventoryItem(inventoryItem);
			SupplierCatalogItem catalogItem = itemRequest.getSupplierCatalogItemId() != null
					? supplierCatalogItemRepository.findById(itemRequest.getSupplierCatalogItemId())
							.orElseThrow(() -> new ResourceNotFoundException(
									"Supplier catalog item not found: " + itemRequest.getSupplierCatalogItemId()))
					: null;
			if (catalogItem != null) {
				if (!catalogItem.getSupplier().getId().equals(purchaseOrder.getSupplier().getId())) {
					throw new BusinessRuleViolationException("Supplier catalog item does not belong to the purchase order supplier");
				}
				if (!catalogItem.getInventoryItem().getId().equals(inventoryItem.getId())) {
					throw new BusinessRuleViolationException("Supplier catalog item does not match the selected inventory item");
				}
			}
			item.setSupplierCatalogItem(catalogItem);
			item.setUnitCode(itemRequest.getUnitCode().trim());
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

	private PurchaseOrderItem resolvePurchaseOrderItem(PurchaseOrder purchaseOrder, UUID purchaseOrderItemId) {
		return purchaseOrder.getItems().stream()
				.filter(item -> item.getId().equals(purchaseOrderItemId))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException(
						"Purchase order item not found in purchase order: " + purchaseOrderItemId));
	}

	private void validateReceiptItem(PurchaseOrderItem purchaseOrderItem, GoodsReceiptItemRequest itemRequest) {
		if (itemRequest.getExpiresAt() != null && itemRequest.getExpiresAt().isBefore(LocalDate.now())) {
			throw new BusinessRuleViolationException("Expired goods cannot be received into stock");
		}
		if (purchaseOrderItem.getInventoryItem().isTrackExpiry() && itemRequest.getExpiresAt() == null) {
			throw new BusinessRuleViolationException("Expiry date is required for items that track expiry");
		}
		BigDecimal remaining = purchaseOrderItem.getQuantity().subtract(purchaseOrderItem.getReceivedQuantity());
		if (itemRequest.getQuantity().compareTo(remaining) > 0) {
			throw new BusinessRuleViolationException(
					"Receipt quantity exceeds remaining purchase order quantity for item " + purchaseOrderItem.getId());
		}
	}

	private StockBatch getOrCreateBatch(
			InventoryItem inventoryItem,
			Warehouse warehouse,
			WarehouseZone warehouseZone,
			String batchNumber,
			LocalDate expiresAt) {
		return stockBatchRepository.findMatchingBatch(
				inventoryItem.getId(),
				warehouse.getId(),
				warehouseZone != null ? warehouseZone.getId() : null,
				batchNumber,
				expiresAt)
				.orElseGet(() -> {
					StockBatch batch = new StockBatch();
					batch.setInventoryItem(inventoryItem);
					batch.setWarehouse(warehouse);
					batch.setWarehouseZone(warehouseZone);
					batch.setBatchNumber(batchNumber);
					batch.setExpiresAt(expiresAt);
					batch.setQuantityOnHand(BigDecimal.ZERO);
					batch.setActive(true);
					return batch;
				});
	}

	private void updatePurchaseOrderStatusAfterReceipt(PurchaseOrder purchaseOrder) {
		boolean allReceived = purchaseOrder.getItems().stream()
				.allMatch(item -> item.getReceivedQuantity().compareTo(item.getQuantity()) >= 0);
		purchaseOrder.setStatus(allReceived ? PurchaseOrderStatus.COMPLETED : PurchaseOrderStatus.PARTIALLY_RECEIVED);
	}

	private void writeInboundMovements(GoodsReceipt goodsReceipt) {
		for (GoodsReceiptItem item : goodsReceipt.getItems()) {
			StockMovement movement = new StockMovement();
			movement.setInventoryItem(item.getInventoryItem());
			movement.setStockBatch(item.getStockBatch());
			movement.setWarehouse(goodsReceipt.getWarehouse());
			movement.setWarehouseZone(goodsReceipt.getWarehouseZone());
			movement.setMovementType(MovementType.INBOUND);
			movement.setQuantity(item.getQuantity());
			movement.setOccurredAt(goodsReceipt.getReceivedAt());
			movement.setReferenceType("goods_receipt");
			movement.setReferenceId(goodsReceipt.getId().toString());
			movement.setNotes("goods_receipt:" + goodsReceipt.getCode());
			stockMovementRepository.save(movement);
		}
	}

	private GoodsReceiptResponse toGoodsReceiptResponse(GoodsReceipt goodsReceipt) {
		GoodsReceiptResponse response = new GoodsReceiptResponse();
		response.setId(goodsReceipt.getId());
		response.setPurchaseOrderId(goodsReceipt.getPurchaseOrder().getId());
		response.setWarehouseId(goodsReceipt.getWarehouse().getId());
		response.setWarehouseZoneId(
				goodsReceipt.getWarehouseZone() != null ? goodsReceipt.getWarehouseZone().getId() : null);
		response.setCode(goodsReceipt.getCode());
		response.setNotes(goodsReceipt.getNotes());
		response.setReceivedAt(goodsReceipt.getReceivedAt());
		response.setCreatedAt(goodsReceipt.getCreatedAt());
		response.setUpdatedAt(goodsReceipt.getUpdatedAt());
		response.setItems(goodsReceipt.getItems().stream().map(this::toGoodsReceiptResponse).toList());
		return response;
	}

	private GoodsReceiptResponse.Item toGoodsReceiptResponse(GoodsReceiptItem item) {
		GoodsReceiptResponse.Item response = new GoodsReceiptResponse.Item();
		response.setId(item.getId());
		response.setPurchaseOrderItemId(item.getPurchaseOrderItem().getId());
		response.setInventoryItemId(item.getInventoryItem().getId());
		response.setStockBatchId(item.getStockBatch().getId());
		response.setBatchNumber(item.getBatchNumber());
		response.setExpiresAt(item.getExpiresAt());
		response.setQuantity(item.getQuantity());
		response.setUnitPrice(item.getUnitPrice());
		return response;
	}
}
