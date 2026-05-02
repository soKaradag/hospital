package com.hospital.inventory.warehouse.mapper;

import org.springframework.stereotype.Component;

import com.hospital.inventory.warehouse.dto.CreateWarehouseRequest;
import com.hospital.inventory.warehouse.dto.CreateWarehouseZoneRequest;
import com.hospital.inventory.warehouse.dto.UpdateWarehouseRequest;
import com.hospital.inventory.warehouse.dto.UpdateWarehouseZoneRequest;
import com.hospital.inventory.warehouse.dto.WarehouseResponse;
import com.hospital.inventory.warehouse.dto.WarehouseZoneResponse;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;

@Component
public class WarehouseMapper {

	public Warehouse toEntity(CreateWarehouseRequest request) {
		Warehouse warehouse = new Warehouse();
		updateWarehouse(request.getCode(), request.getName(), request.getType(), request.getDescription(),
				request.isActive(), warehouse);
		return warehouse;
	}

	public void updateEntity(UpdateWarehouseRequest request, Warehouse warehouse) {
		updateWarehouse(request.getCode(), request.getName(), request.getType(), request.getDescription(),
				request.isActive(), warehouse);
	}

	public WarehouseZone toEntity(CreateWarehouseZoneRequest request) {
		WarehouseZone zone = new WarehouseZone();
		updateZone(request.getCode(), request.getName(), request.getZoneType(), request.isActive(), zone);
		return zone;
	}

	public void updateEntity(UpdateWarehouseZoneRequest request, WarehouseZone zone) {
		updateZone(request.getCode(), request.getName(), request.getZoneType(), request.isActive(), zone);
	}

	private void updateWarehouse(
			String code,
			String name,
			String type,
			String description,
			boolean active,
			Warehouse warehouse) {
		warehouse.setCode(code.trim());
		warehouse.setName(name.trim());
		warehouse.setType(type.trim());
		warehouse.setDescription(description != null ? description.trim() : null);
		warehouse.setActive(active);
	}

	private void updateZone(String code, String name, String zoneType, boolean active, WarehouseZone zone) {
		zone.setCode(code.trim());
		zone.setName(name.trim());
		zone.setZoneType(zoneType.trim());
		zone.setActive(active);
	}

	public WarehouseResponse toResponse(Warehouse warehouse, long zoneCount) {
		WarehouseResponse response = new WarehouseResponse();
		response.setId(warehouse.getId());
		response.setCode(warehouse.getCode());
		response.setName(warehouse.getName());
		response.setType(warehouse.getType());
		response.setDescription(warehouse.getDescription());
		response.setActive(warehouse.isActive());
		response.setZoneCount(zoneCount);
		response.setCreatedAt(warehouse.getCreatedAt());
		response.setUpdatedAt(warehouse.getUpdatedAt());
		return response;
	}

	public WarehouseZoneResponse toResponse(WarehouseZone zone) {
		WarehouseZoneResponse response = new WarehouseZoneResponse();
		response.setId(zone.getId());
		response.setWarehouseId(zone.getWarehouse().getId());
		response.setCode(zone.getCode());
		response.setName(zone.getName());
		response.setZoneType(zone.getZoneType());
		response.setActive(zone.isActive());
		response.setCreatedAt(zone.getCreatedAt());
		response.setUpdatedAt(zone.getUpdatedAt());
		return response;
	}
}
