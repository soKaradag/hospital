package com.hospital.inventory.inventoryitem.model;

import java.math.BigDecimal;

import com.hospital.inventory.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_item_units")
public class InventoryItemUnit extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item_id", nullable = false)
	private InventoryItem inventoryItem;

	@Column(name = "unit_code", nullable = false, length = 50)
	private String unitCode;

	@Column(name = "unit_name", nullable = false, length = 100)
	private String unitName;

	@Column(name = "conversion_factor", nullable = false, precision = 19, scale = 4)
	private BigDecimal conversionFactor;

	@Column(name = "base_unit", nullable = false)
	private boolean baseUnit;

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
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
