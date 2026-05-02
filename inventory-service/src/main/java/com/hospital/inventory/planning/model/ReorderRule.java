package com.hospital.inventory.planning.model;

import java.math.BigDecimal;

import com.hospital.inventory.common.model.BaseEntity;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.supplier.model.Supplier;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reorder_rules")
public class ReorderRule extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item_id", nullable = false)
	private InventoryItem inventoryItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_zone_id")
	private WarehouseZone warehouseZone;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "preferred_supplier_id")
	private Supplier preferredSupplier;

	@Column(name = "min_quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal minQuantity;

	@Column(name = "target_quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal targetQuantity;

	@Column(name = "active", nullable = false)
	private boolean active;

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public WarehouseZone getWarehouseZone() {
		return warehouseZone;
	}

	public void setWarehouseZone(WarehouseZone warehouseZone) {
		this.warehouseZone = warehouseZone;
	}

	public Supplier getPreferredSupplier() {
		return preferredSupplier;
	}

	public void setPreferredSupplier(Supplier preferredSupplier) {
		this.preferredSupplier = preferredSupplier;
	}

	public BigDecimal getMinQuantity() {
		return minQuantity;
	}

	public void setMinQuantity(BigDecimal minQuantity) {
		this.minQuantity = minQuantity;
	}

	public BigDecimal getTargetQuantity() {
		return targetQuantity;
	}

	public void setTargetQuantity(BigDecimal targetQuantity) {
		this.targetQuantity = targetQuantity;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
