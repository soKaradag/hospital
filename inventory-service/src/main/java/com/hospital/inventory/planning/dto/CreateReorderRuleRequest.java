package com.hospital.inventory.planning.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class CreateReorderRuleRequest {

	@NotNull(message = "inventoryItemId must not be null")
	private UUID inventoryItemId;

	@NotNull(message = "warehouseId must not be null")
	private UUID warehouseId;

	private UUID warehouseZoneId;

	private UUID preferredSupplierId;

	@NotNull(message = "minQuantity must not be null")
	@DecimalMin(value = "0.0001", message = "minQuantity must be greater than zero")
	private BigDecimal minQuantity;

	@NotNull(message = "targetQuantity must not be null")
	@DecimalMin(value = "0.0001", message = "targetQuantity must be greater than zero")
	private BigDecimal targetQuantity;

	private boolean active = true;

	public UUID getInventoryItemId() {
		return inventoryItemId;
	}

	public void setInventoryItemId(UUID inventoryItemId) {
		this.inventoryItemId = inventoryItemId;
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

	public UUID getPreferredSupplierId() {
		return preferredSupplierId;
	}

	public void setPreferredSupplierId(UUID preferredSupplierId) {
		this.preferredSupplierId = preferredSupplierId;
	}

	public BigDecimal getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(BigDecimal minQuantity) {
		this.minQuantity = minQuantity;
	}

	public BigDecimal getTargetQuantity() {
		return targetQuantity;
	}

	public void setTargetQuantity(BigDecimal targetQuantity) {
		this.targetQuantity = targetQuantity;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
