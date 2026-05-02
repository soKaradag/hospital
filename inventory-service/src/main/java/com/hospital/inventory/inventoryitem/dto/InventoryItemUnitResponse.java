package com.hospital.inventory.inventoryitem.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class InventoryItemUnitResponse {

	private UUID id;
	private String unitCode;
	private String unitName;
	private BigDecimal conversionFactor;
	private boolean baseUnit;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public BigDecimal getConversionFactor() {
		return conversionFactor;
	}

	public void setConversionFactor(BigDecimal conversionFactor) {
		this.conversionFactor = conversionFactor;
	}

	public boolean isBaseUnit() {
		return baseUnit;
	}

	public void setBaseUnit(boolean baseUnit) {
		this.baseUnit = baseUnit;
	}
}
