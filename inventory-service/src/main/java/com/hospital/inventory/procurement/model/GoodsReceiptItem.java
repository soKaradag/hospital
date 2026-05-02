package com.hospital.inventory.procurement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hospital.inventory.common.model.BaseEntity;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.stock.model.StockBatch;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "goods_receipt_items")
public class GoodsReceiptItem extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "goods_receipt_id", nullable = false)
	private GoodsReceipt goodsReceipt;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "purchase_order_item_id", nullable = false)
	private PurchaseOrderItem purchaseOrderItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "inventory_item_id", nullable = false)
	private InventoryItem inventoryItem;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "stock_batch_id", nullable = false)
	private StockBatch stockBatch;

	@Column(name = "batch_number", nullable = false, length = 100)
	private String batchNumber;

	@Column(name = "expires_at")
	private LocalDate expiresAt;

	@Column(name = "quantity", nullable = false, precision = 19, scale = 4)
	private BigDecimal quantity;

	@Column(name = "unit_price", nullable = false, precision = 19, scale = 4)
	private BigDecimal unitPrice;

	public GoodsReceipt getGoodsReceipt() {
		return goodsReceipt;
	}

	public void setGoodsReceipt(GoodsReceipt goodsReceipt) {
		this.goodsReceipt = goodsReceipt;
	}

	public PurchaseOrderItem getPurchaseOrderItem() {
		return purchaseOrderItem;
	}

	public void setPurchaseOrderItem(PurchaseOrderItem purchaseOrderItem) {
		this.purchaseOrderItem = purchaseOrderItem;
	}

	public InventoryItem getInventoryItem() {
		return inventoryItem;
	}

	public void setInventoryItem(InventoryItem inventoryItem) {
		this.inventoryItem = inventoryItem;
	}

	public StockBatch getStockBatch() {
		return stockBatch;
	}

	public void setStockBatch(StockBatch stockBatch) {
		this.stockBatch = stockBatch;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public LocalDate getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDate expiresAt) {
		this.expiresAt = expiresAt;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
}
