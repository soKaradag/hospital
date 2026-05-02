package com.hospital.inventory.inventoryitem.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class InventoryItemResponse {

	private UUID id;
	private String code;
	private String name;
	private String description;
	private boolean trackBatches;
	private boolean trackExpiry;
	private boolean active;
	private UUID categoryId;
	private String categoryCode;
	private String categoryName;
	private List<InventoryItemUnitResponse> units;
	private List<InventoryItemAliasResponse> aliases;
	private List<InventoryItemBarcodeResponse> barcodes;
	private Instant createdAt;
	private Instant updatedAt;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isTrackBatches() {
		return trackBatches;
	}

	public void setTrackBatches(boolean trackBatches) {
		this.trackBatches = trackBatches;
	}

	public boolean isTrackExpiry() {
		return trackExpiry;
	}

	public void setTrackExpiry(boolean trackExpiry) {
		this.trackExpiry = trackExpiry;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public UUID getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(UUID categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public List<InventoryItemUnitResponse> getUnits() {
		return units;
	}

	public void setUnits(List<InventoryItemUnitResponse> units) {
		this.units = units;
	}

	public List<InventoryItemAliasResponse> getAliases() {
		return aliases;
	}

	public void setAliases(List<InventoryItemAliasResponse> aliases) {
		this.aliases = aliases;
	}

	public List<InventoryItemBarcodeResponse> getBarcodes() {
		return barcodes;
	}

	public void setBarcodes(List<InventoryItemBarcodeResponse> barcodes) {
		this.barcodes = barcodes;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
}
