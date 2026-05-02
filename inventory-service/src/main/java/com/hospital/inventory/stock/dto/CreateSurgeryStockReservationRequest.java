package com.hospital.inventory.stock.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateSurgeryStockReservationRequest {

	@NotNull(message = "surgeryId must not be null")
	private UUID surgeryId;

	@NotBlank(message = "warehouseCode must not be blank")
	@Size(max = 100, message = "warehouseCode must be at most 100 characters")
	private String warehouseCode;

	@Size(max = 100, message = "warehouseZoneCode must be at most 100 characters")
	private String warehouseZoneCode;

	private Instant expiresAt;

	@NotEmpty(message = "items must not be empty")
	@Valid
	private List<SurgeryStockReservationItemRequest> items;

	public UUID getSurgeryId() {
		return surgeryId;
	}

	public void setSurgeryId(UUID surgeryId) {
		this.surgeryId = surgeryId;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getWarehouseZoneCode() {
		return warehouseZoneCode;
	}

	public void setWarehouseZoneCode(String warehouseZoneCode) {
		this.warehouseZoneCode = warehouseZoneCode;
	}

	public Instant getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(Instant expiresAt) {
		this.expiresAt = expiresAt;
	}

	public List<SurgeryStockReservationItemRequest> getItems() {
		return items;
	}

	public void setItems(List<SurgeryStockReservationItemRequest> items) {
		this.items = items;
	}
}
