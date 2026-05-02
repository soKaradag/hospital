package com.hospital.inventory.procurement.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class GoodsReceiptResponse {

	private UUID id;
	private UUID purchaseOrderId;
	private UUID warehouseId;
	private UUID warehouseZoneId;
	private String code;
	private String notes;
	private Instant receivedAt;
	private Instant createdAt;
	private Instant updatedAt;
	private List<Item> items;

	public static class Item {
		private UUID id;
		private UUID purchaseOrderItemId;
		private UUID inventoryItemId;
		private UUID stockBatchId;
		private String batchNumber;
		private LocalDate expiresAt;
		private BigDecimal quantity;
		private BigDecimal unitPrice;

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public UUID getPurchaseOrderItemId() {
			return purchaseOrderItemId;
		}

		public void setPurchaseOrderItemId(UUID purchaseOrderItemId) {
			this.purchaseOrderItemId = purchaseOrderItemId;
		}

		public UUID getInventoryItemId() {
			return inventoryItemId;
		}

		public void setInventoryItemId(UUID inventoryItemId) {
			this.inventoryItemId = inventoryItemId;
		}

		public UUID getStockBatchId() {
			return stockBatchId;
		}

		public void setStockBatchId(UUID stockBatchId) {
			this.stockBatchId = stockBatchId;
		}

		public String getBatchNumber() {
			return batchNumber;
		}

		public void setBatchNumber(String batchNumber) {
			this.batchNumber = batchNumber;
		}

		public LocalDate getExpiresAt() {
			return expiresAt;
		}

		public void setExpiresAt(LocalDate expiresAt) {
			this.expiresAt = expiresAt;
		}

		public BigDecimal getQuantity() {
			return quantity;
		}

		public void setQuantity(BigDecimal quantity) {
			this.quantity = quantity;
		}

		public BigDecimal getUnitPrice() {
			return unitPrice;
		}

		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(UUID purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
	}

	public UUID getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(UUID warehouseId) {
		this.warehouseId = warehouseId;
	}

	public UUID getWarehouseZoneId() {
		return warehouseZoneId;
	}

	public void setWarehouseZoneId(UUID warehouseZoneId) {
		this.warehouseZoneId = warehouseZoneId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Instant getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(Instant receivedAt) {
		this.receivedAt = receivedAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
}
