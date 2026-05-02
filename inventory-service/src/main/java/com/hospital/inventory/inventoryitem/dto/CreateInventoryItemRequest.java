package com.hospital.inventory.inventoryitem.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateInventoryItemRequest {

	@NotBlank(message = "code must not be blank")
	@Size(max = 100, message = "code must be at most 100 characters")
	private String code;

	@NotBlank(message = "name must not be blank")
	@Size(max = 150, message = "name must be at most 150 characters")
	private String name;

	@Size(max = 255, message = "description must be at most 255 characters")
	private String description;

	private boolean trackBatches = true;

	private boolean trackExpiry = true;

	private boolean active = true;

	@NotNull(message = "categoryId must not be null")
	private UUID categoryId;

	@NotEmpty(message = "units must not be empty")
	@Valid
	private List<InventoryItemUnitRequest> units;

	@Valid
	private List<InventoryItemAliasRequest> aliases;

	@Valid
	private List<InventoryItemBarcodeRequest> barcodes;

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

	public List<InventoryItemUnitRequest> getUnits() {
		return units;
	}

	public void setUnits(List<InventoryItemUnitRequest> units) {
		this.units = units;
	}

	public List<InventoryItemAliasRequest> getAliases() {
		return aliases;
	}

	public void setAliases(List<InventoryItemAliasRequest> aliases) {
		this.aliases = aliases;
	}

	public List<InventoryItemBarcodeRequest> getBarcodes() {
		return barcodes;
	}

	public void setBarcodes(List<InventoryItemBarcodeRequest> barcodes) {
		this.barcodes = barcodes;
	}
}
