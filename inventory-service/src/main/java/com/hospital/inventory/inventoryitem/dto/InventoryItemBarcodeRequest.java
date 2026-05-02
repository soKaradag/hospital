package com.hospital.inventory.inventoryitem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InventoryItemBarcodeRequest {

	@NotBlank(message = "barcode must not be blank")
	@Size(max = 150, message = "barcode must be at most 150 characters")
	private String barcode;

	@Size(max = 50, message = "unitCode must be at most 50 characters")
	private String unitCode;

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
