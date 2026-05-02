package com.hospital.inventory.procurement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class GoodsReceiptItemRequest {

	@NotNull(message = "purchaseOrderItemId must not be null")
	private UUID purchaseOrderItemId;

	@NotBlank(message = "batchNumber must not be blank")
	@Size(max = 100, message = "batchNumber must be at most 100 characters")
	private String batchNumber;

	private LocalDate expiresAt;

	@NotNull(message = "quantity must not be null")
	@DecimalMin(value = "0.0001", message = "quantity must be greater than zero")
	private BigDecimal quantity;

	public UUID getPurchaseOrderItemId() {
		return purchaseOrderItemId;
	}

	public void setPurchaseOrderItemId(UUID purchaseOrderItemId) {
		this.purchaseOrderItemId = purchaseOrderItemId;
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

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
}
