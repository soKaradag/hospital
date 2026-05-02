package com.hospital.inventory.procurement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.procurement.dto.CreateGoodsReceiptRequest;
import com.hospital.inventory.procurement.dto.GoodsReceiptItemRequest;
import com.hospital.inventory.procurement.dto.GoodsReceiptResponse;
import com.hospital.inventory.procurement.model.GoodsReceipt;
import com.hospital.inventory.procurement.model.PurchaseOrder;
import com.hospital.inventory.procurement.model.PurchaseOrderItem;
import com.hospital.inventory.procurement.model.PurchaseOrderStatus;
import com.hospital.inventory.procurement.repository.GoodsReceiptRepository;
import com.hospital.inventory.procurement.repository.PurchaseOrderRepository;
import com.hospital.inventory.procurement.repository.SupplierCatalogItemRepository;
import com.hospital.inventory.stock.model.MovementType;
import com.hospital.inventory.stock.model.StockBatch;
import com.hospital.inventory.stock.model.StockMovement;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockMovementRepository;
import com.hospital.inventory.supplier.repository.SupplierRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceImplTest {

	@Mock
	private SupplierRepository supplierRepository;

	@Mock
	private InventoryItemRepository inventoryItemRepository;

	@Mock
	private SupplierCatalogItemRepository supplierCatalogItemRepository;

	@Mock
	private PurchaseOrderRepository purchaseOrderRepository;

	@Mock
	private GoodsReceiptRepository goodsReceiptRepository;

	@Mock
	private WarehouseRepository warehouseRepository;

	@Mock
	private WarehouseZoneRepository warehouseZoneRepository;

	@Mock
	private StockBatchRepository stockBatchRepository;

	@Mock
	private StockMovementRepository stockMovementRepository;

	@InjectMocks
	private ProcurementServiceImpl procurementService;

	@Test
	void createGoodsReceiptShouldCreateInboundMovementAndCompletePurchaseOrder() {
		UUID purchaseOrderId = UUID.randomUUID();
		UUID purchaseOrderItemId = UUID.randomUUID();
		UUID warehouseId = UUID.randomUUID();
		UUID inventoryItemId = UUID.randomUUID();

		InventoryItem inventoryItem = new InventoryItem();
		inventoryItem.setId(inventoryItemId);
		inventoryItem.setTrackExpiry(true);

		PurchaseOrderItem purchaseOrderItem = new PurchaseOrderItem();
		purchaseOrderItem.setId(purchaseOrderItemId);
		purchaseOrderItem.setInventoryItem(inventoryItem);
		purchaseOrderItem.setQuantity(new BigDecimal("5"));
		purchaseOrderItem.setReceivedQuantity(BigDecimal.ZERO);
		purchaseOrderItem.setUnitPrice(new BigDecimal("11.50"));
		purchaseOrderItem.setUnitCode("EA");

		PurchaseOrder purchaseOrder = new PurchaseOrder();
		purchaseOrder.setId(purchaseOrderId);
		purchaseOrder.setStatus(PurchaseOrderStatus.APPROVED);
		purchaseOrder.getItems().add(purchaseOrderItem);

		Warehouse warehouse = new Warehouse();
		warehouse.setId(warehouseId);
		warehouse.setCode("MAIN");
		warehouse.setName("Main Warehouse");

		CreateGoodsReceiptRequest request = new CreateGoodsReceiptRequest();
		request.setPurchaseOrderId(purchaseOrderId);
		request.setWarehouseId(warehouseId);
		request.setCode("GR-TEST");
		GoodsReceiptItemRequest itemRequest = new GoodsReceiptItemRequest();
		itemRequest.setPurchaseOrderItemId(purchaseOrderItemId);
		itemRequest.setBatchNumber("BATCH-001");
		itemRequest.setExpiresAt(LocalDate.now().plusDays(30));
		itemRequest.setQuantity(new BigDecimal("5"));
		request.setItems(java.util.List.of(itemRequest));

		when(goodsReceiptRepository.existsByCodeIgnoreCase("GR-TEST")).thenReturn(false);
		when(purchaseOrderRepository.findById(purchaseOrderId)).thenReturn(Optional.of(purchaseOrder));
		when(warehouseRepository.findById(warehouseId)).thenReturn(Optional.of(warehouse));
		when(stockBatchRepository.findMatchingBatch(
				inventoryItemId,
				warehouseId,
				null,
				"BATCH-001",
				itemRequest.getExpiresAt())).thenReturn(Optional.empty());
		when(stockBatchRepository.save(any(StockBatch.class))).thenAnswer(invocation -> {
			StockBatch batch = invocation.getArgument(0);
			if (batch.getId() == null) {
				batch.setId(UUID.randomUUID());
			}
			return batch;
		});
		when(goodsReceiptRepository.save(any(GoodsReceipt.class))).thenAnswer(invocation -> {
			GoodsReceipt receipt = invocation.getArgument(0);
			receipt.setId(UUID.randomUUID());
			return receipt;
		});
		when(stockMovementRepository.save(any(StockMovement.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GoodsReceiptResponse response = procurementService.createGoodsReceipt(request);

		assertNotNull(response.getId());
		assertEquals("GR-TEST", response.getCode());
		assertEquals(PurchaseOrderStatus.COMPLETED, purchaseOrder.getStatus());
		assertEquals(new BigDecimal("5"), purchaseOrderItem.getReceivedQuantity());

		ArgumentCaptor<StockMovement> movementCaptor = ArgumentCaptor.forClass(StockMovement.class);
		verify(stockMovementRepository).save(movementCaptor.capture());
		StockMovement movement = movementCaptor.getValue();
		assertEquals(MovementType.INBOUND, movement.getMovementType());
		assertEquals("goods_receipt", movement.getReferenceType());
		assertEquals(new BigDecimal("5"), movement.getQuantity());
	}
}
