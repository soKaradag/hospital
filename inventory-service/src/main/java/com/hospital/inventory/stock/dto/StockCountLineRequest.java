package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class StockCountLineRequest {

	@NotNull(message = "itemId must not be null")
	private UUID itemId;

	@NotNull(message = "batchId must not be null")
	private UUID batchId;

	@NotNull(message = "countedQuantity must not be null")
	@DecimalMin(value = "0.0", message = "countedQuantity must be zero or greater")
	private BigDecimal countedQuantity;

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

	public BigDecimal getCountedQuantity() {
		return countedQuantity;
	}

	public void setCountedQuantity(BigDecimal countedQuantity) {
		this.countedQuantity = countedQuantity;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
