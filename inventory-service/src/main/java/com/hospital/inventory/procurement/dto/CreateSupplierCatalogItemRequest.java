package com.hospital.inventory.procurement.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateSupplierCatalogItemRequest {

	@NotNull(message = "inventoryItemId must not be null")
	private UUID inventoryItemId;

	@NotBlank(message = "supplierItemCode must not be blank")
	@Size(max = 100, message = "supplierItemCode must be at most 100 characters")
	private String supplierItemCode;

	@NotBlank(message = "unitCode must not be blank")
	@Size(max = 50, message = "unitCode must be at most 50 characters")
	private String unitCode;

	@NotNull(message = "unitPrice must not be null")
	@DecimalMin(value = "0.0001", message = "unitPrice must be greater than zero")
	private BigDecimal unitPrice;

	private boolean active = true;

	public UUID getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(UUID inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
	}

	public String getSupplierItemCode() {
		return supplierItemCode;
	}

	public void setSupplierItemCode(String supplierItemCode) {
		this.supplierItemCode = supplierItemCode;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
