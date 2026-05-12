package com.hospital.inventory.warehouse.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.inventory.warehouse.dto.CreateWarehouseRequest;
import com.hospital.inventory.warehouse.dto.CreateWarehouseZoneRequest;
import com.hospital.inventory.warehouse.dto.UpdateWarehouseRequest;
import com.hospital.inventory.warehouse.dto.UpdateWarehouseZoneRequest;
import com.hospital.inventory.warehouse.dto.WarehouseResponse;
import com.hospital.inventory.warehouse.dto.WarehouseZoneResponse;

public interface WarehouseService {

	WarehouseResponse create(CreateWarehouseRequest request);

	WarehouseResponse update(UUID id, UpdateWarehouseRequest request);

	WarehouseResponse getById(UUID id);

	Page<WarehouseResponse> getAll(Pageable pageable);

	Page<WarehouseResponse> search(String keyword, Pageable pageable);

	void delete(UUID id);

	WarehouseZoneResponse createZone(UUID warehouseId, CreateWarehouseZoneRequest request);

	WarehouseZoneResponse updateZone(UUID warehouseId, UUID zoneId, UpdateWarehouseZoneRequest request);

	WarehouseZoneResponse getZoneById(UUID warehouseId, UUID zoneId);

	Page<WarehouseZoneResponse> getZones(UUID warehouseId, Pageable pageable);

	void deleteZone(UUID warehouseId, UUID zoneId);
}
