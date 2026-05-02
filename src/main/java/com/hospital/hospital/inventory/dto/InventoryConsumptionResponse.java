package com.hospital.hospital.inventory.dto;

import java.math.BigDecimal;

public class InventoryConsumptionResponse {

	private String inventoryItemCode;
	private String warehouseCode;
	private String warehouseZoneCode;
	private BigDecimal requestedQuantity;
	private BigDecimal consumedQuantity;

	public String getInventoryItemCode() {
		return inventoryItemCode;
	}

	public void setInventoryItemCode(String inventoryItemCode) {
		this.inventoryItemCode = inventoryItemCode;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getWarehouseZoneCode() {
		return warehouseZoneCode;
	}

	public void setWarehouseZoneCode(String warehouseZoneCode) {
		this.warehouseZoneCode = warehouseZoneCode;
	}

	public BigDecimal getRequestedQuantity() {
		return requestedQuantity;
	}

	public void setRequestedQuantity(BigDecimal requestedQuantity) {
		this.requestedQuantity = requestedQuantity;
	}

	public BigDecimal getConsumedQuantity() {
		return consumedQuantity;
	}

	public void setConsumedQuantity(BigDecimal consumedQuantity) {
		this.consumedQuantity = consumedQuantity;
	}
}
