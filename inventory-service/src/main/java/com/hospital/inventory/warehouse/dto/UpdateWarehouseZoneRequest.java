package com.hospital.inventory.warehouse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateWarehouseZoneRequest {

	@NotBlank(message = "code must not be blank")
	@Size(max = 100, message = "code must be at most 100 characters")
	private String code;

	@NotBlank(message = "name must not be blank")
	@Size(max = 150, message = "name must be at most 150 characters")
	private String name;

	@NotBlank(message = "zoneType must not be blank")
	@Size(max = 80, message = "zoneType must be at most 80 characters")
	private String zoneType;

	private boolean active = true;

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

	public String getZoneType() {
		return zoneType;
	}

	public void setZoneType(String zoneType) {
		this.zoneType = zoneType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}
