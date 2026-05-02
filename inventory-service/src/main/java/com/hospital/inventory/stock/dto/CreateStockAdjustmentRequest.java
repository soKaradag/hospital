package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateStockAdjustmentRequest {

	@NotNull(message = "itemId must not be null")
	private UUID itemId;

	@NotNull(message = "batchId must not be null")
	private UUID batchId;

	@NotNull(message = "warehouseId must not be null")
	private UUID warehouseId;

	private UUID warehouseZoneId;

	@NotNull(message = "quantityDelta must not be null")
	private BigDecimal quantityDelta;

	@NotBlank(message = "reasonCode must not be blank")
	@Size(max = 80, message = "reasonCode must be at most 80 characters")
	private String reasonCode;

	@Size(max = 255, message = "notes must be at most 255 characters")
	private String notes;

	public UUID getItemId() {
		return itemId;
	}

	public void setItemId(UUID itemId) {
		this.itemId = itemId;
	}

	public UUID getBatchId() {
		return batchId;
	}

	public void setBatchId(UUID batchId) {
		this.batchId = batchId;
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

	public BigDecimal getQuantityDelta() {
		return quantityDelta;
	}

	public void setQuantityDelta(BigDecimal quantityDelta) {
		this.quantityDelta = quantityDelta;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
