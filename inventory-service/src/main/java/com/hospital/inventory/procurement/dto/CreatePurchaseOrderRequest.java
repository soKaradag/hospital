package com.hospital.inventory.procurement.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePurchaseOrderRequest {

	@NotNull(message = "supplierId must not be null")
	private UUID supplierId;

	@NotBlank(message = "code must not be blank")
	@Size(max = 100, message = "code must be at most 100 characters")
	private String code;

	@Size(max = 255, message = "notes must be at most 255 characters")
	private String notes;

	@NotEmpty(message = "items must not be empty")
	@Valid
	private List<PurchaseOrderItemRequest> items;

	public UUID getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(UUID supplierId) {
		this.supplierId = supplierId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<PurchaseOrderItemRequest> getItems() {
		return items;
	}

	public void setItems(List<PurchaseOrderItemRequest> items) {
		this.items = items;
	}
}
