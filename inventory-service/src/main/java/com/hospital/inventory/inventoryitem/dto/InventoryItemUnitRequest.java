package com.hospital.inventory.inventoryitem.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class InventoryItemUnitRequest {

	@NotBlank(message = "unitCode must not be blank")
	@Size(max = 50, message = "unitCode must be at most 50 characters")
	private String unitCode;

	@NotBlank(message = "unitName must not be blank")
	@Size(max = 100, message = "unitName must be at most 100 characters")
	private String unitName;

	@NotNull(message = "conversionFactor must not be null")
	@DecimalMin(value = "0.0001", message = "conversionFactor must be greater than zero")
	private BigDecimal conversionFactor;

	private boolean baseUnit;

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
