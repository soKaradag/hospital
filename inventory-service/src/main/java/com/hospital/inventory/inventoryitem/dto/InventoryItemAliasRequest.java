package com.hospital.inventory.inventoryitem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class InventoryItemAliasRequest {

	@NotBlank(message = "alias must not be blank")
	@Size(max = 150, message = "alias must be at most 150 characters")
	private String alias;

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
}
