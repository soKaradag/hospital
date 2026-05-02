package com.hospital.inventory.stock.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateStockReservationRequest {

	@NotNull(message = "itemId must not be null")
	private UUID itemId;

	private UUID batchId;

	@NotNull(message = "warehouseId must not be null")
	private UUID warehouseId;

	private UUID warehouseZoneId;

	@NotNull(message = "quantity must not be null")
	@DecimalMin(value = "0.0001", message = "quantity must be greater than zero")
	private BigDecimal quantity;

	@NotBlank(message = "reservationType must not be blank")
	@Size(max = 80, message = "reservationType must be at most 80 characters")
	private String reservationType;

	@Size(max = 80, message = "referenceType must be at most 80 characters")
	private String referenceType;

	@Size(max = 80, message = "referenceId must be at most 80 characters")
	private String referenceId;

	private Instant expiresAt;

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

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getReservationType() {
		return reservationType;
	}

	public void setReservationType(String reservationType) {
		this.reservationType = reservationType;
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

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}
