package com.hospital.inventory.auth.model;

public final class PermissionCodes {

	public static final String INVENTORY_ITEMS_READ = "inventory.items.read";
	public static final String INVENTORY_ITEMS_WRITE = "inventory.items.write";
	public static final String INVENTORY_WAREHOUSES_READ = "inventory.warehouses.read";
	public static final String INVENTORY_WAREHOUSES_WRITE = "inventory.warehouses.write";
	public static final String INVENTORY_SUPPLIERS_READ = "inventory.suppliers.read";
	public static final String INVENTORY_SUPPLIERS_WRITE = "inventory.suppliers.write";
	public static final String INVENTORY_STOCK_READ = "inventory.stock.read";
	public static final String INVENTORY_STOCK_ADJUST = "inventory.stock.adjust";
	public static final String INVENTORY_STOCK_TRANSFER = "inventory.stock.transfer";
	public static final String INVENTORY_STOCK_RESERVE = "inventory.stock.reserve";
	public static final String INVENTORY_STOCK_CONSUME = "inventory.stock.consume";
	public static final String INVENTORY_PURCHASE_READ = "inventory.purchase.read";
	public static final String INVENTORY_PURCHASE_WRITE = "inventory.purchase.write";
	public static final String INVENTORY_RECEIPTS_WRITE = "inventory.receipts.write";
	public static final String INVENTORY_COUNTS_MANAGE = "inventory.counts.manage";

	private PermissionCodes() {
	}
}
