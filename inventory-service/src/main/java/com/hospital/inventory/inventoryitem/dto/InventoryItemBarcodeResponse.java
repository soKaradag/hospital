package com.hospital.inventory.inventoryitem.dto;

import java.util.UUID;

public class InventoryItemBarcodeResponse {

	private UUID id;
	private String barcode;
	private String unitCode;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
}
