package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class StockMovementResponse {

	private UUID id;
	private UUID itemId;
	private UUID batchId;
	private UUID warehouseId;
	private UUID warehouseZoneId;
	private String movementType;
	private BigDecimal quantity;
	private Instant occurredAt;
	private String referenceType;
	private String referenceId;
	private String notes;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

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

	public String getMovementType() {
		return movementType;
	}

	public void setMovementType(String movementType) {
		this.movementType = movementType;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public Instant getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(Instant occurredAt) {
		this.occurredAt = occurredAt;
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
