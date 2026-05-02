package com.hospital.inventory.inventoryitem.model;

import java.util.ArrayList;
import java.util.List;

import com.hospital.inventory.common.model.BaseEntity;
import com.hospital.inventory.inventorycategory.model.InventoryCategory;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_items")
public class InventoryItem extends BaseEntity {

	@Column(name = "code", nullable = false, length = 100, unique = true)
	private String code;

	@Column(name = "name", nullable = false, length = 150)
	private String name;

	@Column(name = "description", length = 255)
	private String description;

	@Column(name = "track_batches", nullable = false)
	private boolean trackBatches;

	@Column(name = "track_expiry", nullable = false)
	private boolean trackExpiry;

	@Column(name = "active", nullable = false)
	private boolean active;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private InventoryCategory category;

	@OneToMany(mappedBy = "inventoryItem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InventoryItemUnit> units = new ArrayList<>();

	@OneToMany(mappedBy = "inventoryItem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InventoryItemAlias> aliases = new ArrayList<>();

	@OneToMany(mappedBy = "inventoryItem", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InventoryItemBarcode> barcodes = new ArrayList<>();

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

	public InventoryCategory getCategory() {
		return category;
	}

	public void setCategory(InventoryCategory category) {
		this.category = category;
	}

	public List<InventoryItemUnit> getUnits() {
		return units;
	}

	public void setUnits(List<InventoryItemUnit> units) {
		this.units = units;
	}

	public List<InventoryItemAlias> getAliases() {
		return aliases;
	}

	public void setAliases(List<InventoryItemAlias> aliases) {
		this.aliases = aliases;
	}

	public List<InventoryItemBarcode> getBarcodes() {
		return barcodes;
	}

	public void setBarcodes(List<InventoryItemBarcode> barcodes) {
		this.barcodes = barcodes;
	}
}
