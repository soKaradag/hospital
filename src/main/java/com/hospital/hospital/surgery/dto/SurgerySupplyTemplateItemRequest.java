package com.hospital.hospital.surgery.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SurgerySupplyTemplateItemRequest {

	@NotBlank(message = "inventoryItemCode must not be blank")
	@Size(max = 100, message = "inventoryItemCode must be at most 100 characters")
	private String inventoryItemCode;

	@NotNull(message = "quantity must not be null")
	@DecimalMin(value = "0.0001", message = "quantity must be greater than zero")
	private BigDecimal quantity;

	@Size(max = 255, message = "note must be at most 255 characters")
	private String note;

	public String getInventoryItemCode() {
		return inventoryItemCode;
	}

	public void setInventoryItemCode(String inventoryItemCode) {
		this.inventoryItemCode = inventoryItemCode;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
