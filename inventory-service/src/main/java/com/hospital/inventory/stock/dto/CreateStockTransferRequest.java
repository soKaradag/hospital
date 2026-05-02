package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateStockTransferRequest {

	@NotNull(message = "itemId must not be null")
	private UUID itemId;

	@NotNull(message = "batchId must not be null")
	private UUID batchId;

	@NotNull(message = "fromWarehouseId must not be null")
	private UUID fromWarehouseId;

	private UUID fromWarehouseZoneId;

	@NotNull(message = "toWarehouseId must not be null")
	private UUID toWarehouseId;

	private UUID toWarehouseZoneId;

	@NotNull(message = "quantity must not be null")
	@DecimalMin(value = "0.0001", message = "quantity must be greater than zero")
	private BigDecimal quantity;

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

	public UUID getFromWarehouseId() {
		return fromWarehouseId;
	}

	public void setFromWarehouseId(UUID fromWarehouseId) {
		this.fromWarehouseId = fromWarehouseId;
	}

	public UUID getFromWarehouseZoneId() {
		return fromWarehouseZoneId;
	}

	public void setFromWarehouseZoneId(UUID fromWarehouseZoneId) {
		this.fromWarehouseZoneId = fromWarehouseZoneId;
	}

	public UUID getToWarehouseId() {
		return toWarehouseId;
	}

	public void setToWarehouseId(UUID toWarehouseId) {
		this.toWarehouseId = toWarehouseId;
	}

	public UUID getToWarehouseZoneId() {
		return toWarehouseZoneId;
	}

	public void setToWarehouseZoneId(UUID toWarehouseZoneId) {
		this.toWarehouseZoneId = toWarehouseZoneId;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
