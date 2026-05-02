package com.hospital.inventory.warehouse.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.DuplicateResourceException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.warehouse.dto.CreateWarehouseRequest;
import com.hospital.inventory.warehouse.dto.CreateWarehouseZoneRequest;
import com.hospital.inventory.warehouse.dto.UpdateWarehouseRequest;
import com.hospital.inventory.warehouse.dto.UpdateWarehouseZoneRequest;
import com.hospital.inventory.warehouse.dto.WarehouseResponse;
import com.hospital.inventory.warehouse.dto.WarehouseZoneResponse;
import com.hospital.inventory.warehouse.mapper.WarehouseMapper;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@Service
public class WarehouseServiceImpl implements WarehouseService {

	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;
	private final WarehouseMapper warehouseMapper;

	public WarehouseServiceImpl(
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository,
			WarehouseMapper warehouseMapper) {
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
		this.warehouseMapper = warehouseMapper;
	}

	@Override
	@Transactional
	public WarehouseResponse create(CreateWarehouseRequest request) {
		if (warehouseRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
			throw new DuplicateResourceException("Warehouse code already exists: " + request.getCode());
		}
		Warehouse warehouse = warehouseRepository.save(warehouseMapper.toEntity(request));
		return toResponse(warehouse);
	}

	@Override
	@Transactional
	public WarehouseResponse update(UUID id, UpdateWarehouseRequest request) {
		Warehouse warehouse = getWarehouse(id);
		warehouseRepository.findByCodeIgnoreCase(request.getCode().trim())
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new DuplicateResourceException("Warehouse code already exists: " + request.getCode());
				});
		warehouseMapper.updateEntity(request, warehouse);
		return toResponse(warehouseRepository.save(warehouse));
	}

	@Override
	@Transactional(readOnly = true)
	public WarehouseResponse getById(UUID id) {
		return toResponse(getWarehouse(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<WarehouseResponse> getAll(Pageable pageable) {
		return warehouseRepository.findAll(pageable).map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<WarehouseResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String trimmedKeyword = keyword.trim();
		return warehouseRepository.findAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
				trimmedKeyword,
				trimmedKeyword,
				pageable).map(this::toResponse);
	}

	@Override
	@Transactional
	public WarehouseZoneResponse createZone(UUID warehouseId, CreateWarehouseZoneRequest request) {
		Warehouse warehouse = getWarehouse(warehouseId);
		if (warehouseZoneRepository.existsByWarehouseIdAndCodeIgnoreCase(warehouseId, request.getCode().trim())) {
			throw new DuplicateResourceException("Warehouse zone code already exists in warehouse: " + request.getCode());
		}

		WarehouseZone zone = warehouseMapper.toEntity(request);
		zone.setWarehouse(warehouse);
		return warehouseMapper.toResponse(warehouseZoneRepository.save(zone));
	}

	@Override
	@Transactional
	public WarehouseZoneResponse updateZone(UUID warehouseId, UUID zoneId, UpdateWarehouseZoneRequest request) {
		WarehouseZone zone = getZone(warehouseId, zoneId);
		warehouseZoneRepository.findByWarehouseIdAndCodeIgnoreCase(warehouseId, request.getCode().trim())
				.filter(existing -> !existing.getId().equals(zoneId))
				.ifPresent(existing -> {
					throw new DuplicateResourceException(
							"Warehouse zone code already exists in warehouse: " + request.getCode());
				});
		warehouseMapper.updateEntity(request, zone);
		return warehouseMapper.toResponse(warehouseZoneRepository.save(zone));
	}

	@Override
	@Transactional(readOnly = true)
	public WarehouseZoneResponse getZoneById(UUID warehouseId, UUID zoneId) {
		return warehouseMapper.toResponse(getZone(warehouseId, zoneId));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<WarehouseZoneResponse> getZones(UUID warehouseId, Pageable pageable) {
		getWarehouse(warehouseId);
		return warehouseZoneRepository.findAllByWarehouseId(warehouseId, pageable).map(warehouseMapper::toResponse);
	}

	private Warehouse getWarehouse(UUID id) {
		return warehouseRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + id));
	}

	private WarehouseZone getZone(UUID warehouseId, UUID zoneId) {
		WarehouseZone zone = warehouseZoneRepository.findById(zoneId)
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse zone not found: " + zoneId));
		if (!zone.getWarehouse().getId().equals(warehouseId)) {
			throw new ResourceNotFoundException("Warehouse zone not found in warehouse: " + zoneId);
		}
		return zone;
	}

	private WarehouseResponse toResponse(Warehouse warehouse) {
		return warehouseMapper.toResponse(warehouse, warehouseZoneRepository.countByWarehouseId(warehouse.getId()));
	}
}
