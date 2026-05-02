package com.hospital.hospital.inventory.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class SurgeryInventoryReservationRequest {

	private UUID surgeryId;
	private String warehouseCode;
	private String warehouseZoneCode;
	private Instant expiresAt;
	private List<SurgeryInventoryReservationItemRequest> items;

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

	public List<SurgeryInventoryReservationItemRequest> getItems() {
		return items;
	}

	public void setItems(List<SurgeryInventoryReservationItemRequest> items) {
		this.items = items;
	}
}
