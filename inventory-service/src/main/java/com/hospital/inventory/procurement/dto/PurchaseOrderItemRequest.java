package com.hospital.inventory.procurement.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PurchaseOrderItemRequest {

	@NotNull(message = "inventoryItemId must not be null")
	private UUID inventoryItemId;

	private UUID supplierCatalogItemId;

	@NotNull(message = "quantity must not be null")
	@DecimalMin(value = "0.0001", message = "quantity must be greater than zero")
	private BigDecimal quantity;

	@NotNull(message = "unitPrice must not be null")
	@DecimalMin(value = "0.0001", message = "unitPrice must be greater than zero")
	private BigDecimal unitPrice;

	@Size(max = 50, message = "unitCode must be at most 50 characters")
	private String unitCode;

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

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
}
