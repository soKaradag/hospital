package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateStockConsumptionRequest {

	@NotBlank(message = "inventoryItemCode must not be blank")
	@Size(max = 100, message = "inventoryItemCode must be at most 100 characters")
	private String inventoryItemCode;

	@NotBlank(message = "warehouseCode must not be blank")
	@Size(max = 100, message = "warehouseCode must be at most 100 characters")
	private String warehouseCode;

	@Size(max = 100, message = "warehouseZoneCode must be at most 100 characters")
	private String warehouseZoneCode;

	@Size(max = 100, message = "batchNumber must be at most 100 characters")
	private String batchNumber;

	@NotNull(message = "quantity must not be null")
	@DecimalMin(value = "0.0001", message = "quantity must be greater than zero")
	private BigDecimal quantity;

	@Size(max = 80, message = "referenceType must be at most 80 characters")
	private String referenceType;

	@Size(max = 80, message = "referenceId must be at most 80 characters")
	private String referenceId;

	@Size(max = 255, message = "notes must be at most 255 characters")
	private String notes;

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

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(String referenceType) {
		this.referenceType = referenceType;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
