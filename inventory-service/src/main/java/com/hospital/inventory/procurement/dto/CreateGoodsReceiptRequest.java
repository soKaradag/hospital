package com.hospital.inventory.procurement.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateGoodsReceiptRequest {

	@NotNull(message = "purchaseOrderId must not be null")
	private UUID purchaseOrderId;

	@NotNull(message = "warehouseId must not be null")
	private UUID warehouseId;

	private UUID warehouseZoneId;

	@NotBlank(message = "code must not be blank")
	@Size(max = 100, message = "code must be at most 100 characters")
	private String code;

	@Size(max = 255, message = "notes must be at most 255 characters")
	private String notes;

	@NotEmpty(message = "items must not be empty")
	@Valid
	private List<GoodsReceiptItemRequest> items;

	public UUID getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(UUID purchaseOrderId) {
		this.purchaseOrderId = purchaseOrderId;
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

	public List<GoodsReceiptItemRequest> getItems() {
		return items;
	}

	public void setItems(List<GoodsReceiptItemRequest> items) {
		this.items = items;
	}
}
