package com.hospital.inventory.stock.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateStockCountRequest {

	@NotNull(message = "warehouseId must not be null")
	private UUID warehouseId;

	private UUID warehouseZoneId;

	@Size(max = 255, message = "notes must be at most 255 characters")
	private String notes;

	@NotEmpty(message = "lines must not be empty")
	@Valid
	private List<StockCountLineRequest> lines;

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

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<StockCountLineRequest> getLines() {
		return lines;
	}

	public void setLines(List<StockCountLineRequest> lines) {
		this.lines = lines;
	}
}
