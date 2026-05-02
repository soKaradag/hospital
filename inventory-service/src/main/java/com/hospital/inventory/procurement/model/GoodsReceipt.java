package com.hospital.inventory.procurement.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.hospital.inventory.common.model.BaseEntity;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "goods_receipts")
public class GoodsReceipt extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "purchase_order_id", nullable = false)
	private PurchaseOrder purchaseOrder;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "warehouse_id", nullable = false)
	private Warehouse warehouse;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "warehouse_zone_id")
	private WarehouseZone warehouseZone;

	@Column(name = "code", nullable = false, length = 100, unique = true)
	private String code;

	@Column(name = "notes", length = 255)
	private String notes;

	@Column(name = "received_at", nullable = false)
	private Instant receivedAt;

	@OneToMany(mappedBy = "goodsReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<GoodsReceiptItem> items = new ArrayList<>();

	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public Instant getReceivedAt() {
		return receivedAt;
	}

	public void setReceivedAt(Instant receivedAt) {
		this.receivedAt = receivedAt;
	}

	public List<GoodsReceiptItem> getItems() {
		return items;
	}

	public void setItems(List<GoodsReceiptItem> items) {
		this.items = items;
	}
}
