package com.hospital.inventory.procurement.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class PurchaseOrderResponse {

	private UUID id;
	private UUID supplierId;
	private String code;
	private String status;
	private String notes;
	private Instant createdAt;
	private Instant updatedAt;
	private List<Item> items;

	public static class Item {
		private UUID id;
		private UUID inventoryItemId;
		private UUID supplierCatalogItemId;
		private String unitCode;
		private BigDecimal quantity;
		private BigDecimal unitPrice;
		private BigDecimal receivedQuantity;

		public UUID getId() {
			return id;
		}

		public void setId(UUID id) {
			this.id = id;
		}

		public UUID getInventoryItemId() {
			return inventoryItemId;
		}

		public void setInventoryItemId(UUID inventoryItemId) {
			this.inventoryItemId = inventoryItemId;
		}

		public UUID getSupplierCatalogItemId() {
			return supplierCatalogItemId;
		}

		public void setSupplierCatalogItemId(UUID supplierCatalogItemId) {
			this.supplierCatalogItemId = supplierCatalogItemId;
		}

		public String getUnitCode() {
			return unitCode;
		}

		public void setUnitCode(String unitCode) {
			this.unitCode = unitCode;
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

		public BigDecimal getReceivedQuantity() {
			return receivedQuantity;
		}

		public void setReceivedQuantity(BigDecimal receivedQuantity) {
			this.receivedQuantity = receivedQuantity;
		}
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public UUID getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(UUID supplierId) {
		this.supplierId = supplierId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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
