package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class StockAvailabilityBatchResponse {

	private UUID batchId;
	private UUID warehouseId;
	private UUID warehouseZoneId;
	private String batchNumber;
	private LocalDate expiresAt;
	private BigDecimal quantityOnHand;

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

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public LocalDate getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDate expiresAt) {
		this.expiresAt = expiresAt;
	}

	public BigDecimal getQuantityOnHand() {
		return quantityOnHand;
	}

	public void setQuantityOnHand(BigDecimal quantityOnHand) {
		this.quantityOnHand = quantityOnHand;
	}
}
