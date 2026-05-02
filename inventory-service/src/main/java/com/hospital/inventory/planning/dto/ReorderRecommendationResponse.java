package com.hospital.inventory.planning.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class ReorderRecommendationResponse {

	private UUID reorderRuleId;
	private UUID inventoryItemId;
	private UUID warehouseId;
	private UUID warehouseZoneId;
	private UUID preferredSupplierId;
	private BigDecimal minQuantity;
	private BigDecimal targetQuantity;
	private BigDecimal onHandQuantity;
	private BigDecimal reservedQuantity;
	private BigDecimal availableQuantity;
	private BigDecimal shortageQuantity;
	private BigDecimal suggestedOrderQuantity;

	public UUID getReorderRuleId() {
		return reorderRuleId;
	}

	public void setReorderRuleId(UUID reorderRuleId) {
		this.reorderRuleId = reorderRuleId;
	}

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

	public BigDecimal getOnHandQuantity() {
		return onHandQuantity;
	}

	public void setOnHandQuantity(BigDecimal onHandQuantity) {
		this.onHandQuantity = onHandQuantity;
	}

	public BigDecimal getReservedQuantity() {
		return reservedQuantity;
	}

	public void setReservedQuantity(BigDecimal reservedQuantity) {
		this.reservedQuantity = reservedQuantity;
	}

	public BigDecimal getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(BigDecimal availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public BigDecimal getShortageQuantity() {
		return shortageQuantity;
	}

	public void setShortageQuantity(BigDecimal shortageQuantity) {
		this.shortageQuantity = shortageQuantity;
	}

	public BigDecimal getSuggestedOrderQuantity() {
		return suggestedOrderQuantity;
	}

	public void setSuggestedOrderQuantity(BigDecimal suggestedOrderQuantity) {
		this.suggestedOrderQuantity = suggestedOrderQuantity;
	}
}
