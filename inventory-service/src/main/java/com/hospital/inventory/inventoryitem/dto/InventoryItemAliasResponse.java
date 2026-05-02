package com.hospital.inventory.inventoryitem.dto;

import java.util.UUID;

public class InventoryItemAliasResponse {

	private UUID id;
	private String alias;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
