package com.hospital.inventory.inventoryitem.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.DuplicateResourceException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventorycategory.model.InventoryCategory;
import com.hospital.inventory.inventorycategory.repository.InventoryCategoryRepository;
import com.hospital.inventory.inventoryitem.dto.CreateInventoryItemRequest;
import com.hospital.inventory.inventoryitem.dto.InventoryItemAliasRequest;
import com.hospital.inventory.inventoryitem.dto.InventoryItemBarcodeRequest;
import com.hospital.inventory.inventoryitem.dto.InventoryItemResponse;
import com.hospital.inventory.inventoryitem.dto.InventoryItemUnitRequest;
import com.hospital.inventory.inventoryitem.dto.UpdateInventoryItemRequest;
import com.hospital.inventory.inventoryitem.mapper.InventoryItemMapper;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.model.InventoryItemAlias;
import com.hospital.inventory.inventoryitem.model.InventoryItemBarcode;
import com.hospital.inventory.inventoryitem.model.InventoryItemUnit;
import com.hospital.inventory.inventoryitem.repository.InventoryItemAliasRepository;
import com.hospital.inventory.inventoryitem.repository.InventoryItemBarcodeRepository;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;

@Service
public class InventoryItemServiceImpl implements InventoryItemService {

	private final InventoryItemRepository inventoryItemRepository;
	private final InventoryItemAliasRepository inventoryItemAliasRepository;
	private final InventoryItemBarcodeRepository inventoryItemBarcodeRepository;
	private final InventoryCategoryRepository inventoryCategoryRepository;
	private final InventoryItemMapper inventoryItemMapper;

	public InventoryItemServiceImpl(
			InventoryItemRepository inventoryItemRepository,
			InventoryItemAliasRepository inventoryItemAliasRepository,
			InventoryItemBarcodeRepository inventoryItemBarcodeRepository,
			InventoryCategoryRepository inventoryCategoryRepository,
			InventoryItemMapper inventoryItemMapper) {
		this.inventoryItemRepository = inventoryItemRepository;
		this.inventoryItemAliasRepository = inventoryItemAliasRepository;
		this.inventoryItemBarcodeRepository = inventoryItemBarcodeRepository;
		this.inventoryCategoryRepository = inventoryCategoryRepository;
		this.inventoryItemMapper = inventoryItemMapper;
	}

	@Override
	@Transactional
	public InventoryItemResponse create(CreateInventoryItemRequest request) {
		if (inventoryItemRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
			throw new DuplicateResourceException("Inventory item code already exists: " + request.getCode());
		}

		validateRequest(request.getUnits(), request.getAliases(), request.getBarcodes(), null);
		InventoryItem item = inventoryItemMapper.toEntity(request);
		item.setCategory(getCategory(request.getCategoryId()));
		syncChildren(item, request.getUnits(), request.getAliases(), request.getBarcodes());
		return inventoryItemMapper.toResponse(inventoryItemRepository.save(item));
	}

	@Override
	@Transactional
	public InventoryItemResponse update(UUID id, UpdateInventoryItemRequest request) {
		InventoryItem item = getItem(id);
		inventoryItemRepository.findByCodeIgnoreCase(request.getCode().trim())
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new DuplicateResourceException("Inventory item code already exists: " + request.getCode());
				});

		validateRequest(request.getUnits(), request.getAliases(), request.getBarcodes(), item.getId());
		inventoryItemMapper.updateEntity(request, item);
		item.setCategory(getCategory(request.getCategoryId()));
		syncChildren(item, request.getUnits(), request.getAliases(), request.getBarcodes());
		return inventoryItemMapper.toResponse(inventoryItemRepository.save(item));
	}

	@Override
	@Transactional(readOnly = true)
	public InventoryItemResponse getById(UUID id) {
		return inventoryItemMapper.toResponse(getItem(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryItemResponse> getAll(Pageable pageable) {
		return inventoryItemRepository.findAll(pageable).map(inventoryItemMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryItemResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		return inventoryItemRepository.search(keyword.trim(), pageable).map(inventoryItemMapper::toResponse);
	}

	private InventoryItem getItem(UUID id) {
		return inventoryItemRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + id));
	}

	private InventoryCategory getCategory(UUID id) {
		return inventoryCategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory category not found: " + id));
	}

	private void validateRequest(
			List<InventoryItemUnitRequest> units,
			List<InventoryItemAliasRequest> aliases,
			List<InventoryItemBarcodeRequest> barcodes,
			UUID currentItemId) {
		validateUnits(units);
		validateAliases(aliases, currentItemId);
		validateBarcodes(barcodes, currentItemId);
	}

	private void validateUnits(List<InventoryItemUnitRequest> units) {
		if (units == null || units.isEmpty()) {
			throw new BusinessRuleViolationException("At least one unit definition is required");
		}

		Set<String> unitCodes = new HashSet<>();
		long baseUnitCount = 0;
		for (InventoryItemUnitRequest unit : units) {
			String normalizedCode = unit.getUnitCode().trim().toLowerCase();
			if (!unitCodes.add(normalizedCode)) {
				throw new BusinessRuleViolationException("Duplicate unitCode in request: " + unit.getUnitCode());
			}
			if (unit.isBaseUnit()) {
				baseUnitCount++;
				if (unit.getConversionFactor().compareTo(BigDecimal.ONE) != 0) {
					throw new BusinessRuleViolationException("Base unit conversionFactor must be exactly 1");
				}
			}
		}

		if (baseUnitCount != 1) {
			throw new BusinessRuleViolationException("Exactly one base unit must be defined");
		}
	}

	private void validateAliases(List<InventoryItemAliasRequest> aliases, UUID currentItemId) {
		if (aliases == null) {
			return;
		}

		Set<String> normalizedAliases = new HashSet<>();
		for (InventoryItemAliasRequest alias : aliases) {
			String normalizedAlias = alias.getAlias().trim().toLowerCase();
			if (!normalizedAliases.add(normalizedAlias)) {
				throw new BusinessRuleViolationException("Duplicate alias in request: " + alias.getAlias());
			}
			inventoryItemAliasRepository.findByAliasIgnoreCase(alias.getAlias().trim())
					.filter(existing -> currentItemId == null || !existing.getInventoryItem().getId().equals(currentItemId))
					.ifPresent(existing -> {
						throw new DuplicateResourceException("Inventory item alias already exists: " + alias.getAlias());
					});
		}
	}

	private void validateBarcodes(List<InventoryItemBarcodeRequest> barcodes, UUID currentItemId) {
		if (barcodes == null) {
			return;
		}

		Set<String> normalizedBarcodes = new HashSet<>();
		for (InventoryItemBarcodeRequest barcode : barcodes) {
			String normalizedBarcode = barcode.getBarcode().trim().toLowerCase();
			if (!normalizedBarcodes.add(normalizedBarcode)) {
				throw new BusinessRuleViolationException("Duplicate barcode in request: " + barcode.getBarcode());
			}
			inventoryItemBarcodeRepository.findByBarcodeIgnoreCase(barcode.getBarcode().trim())
					.filter(existing -> currentItemId == null || !existing.getInventoryItem().getId().equals(currentItemId))
					.ifPresent(existing -> {
						throw new DuplicateResourceException(
								"Inventory item barcode already exists: " + barcode.getBarcode());
					});
		}
	}

	private void syncChildren(
			InventoryItem item,
			List<InventoryItemUnitRequest> units,
			List<InventoryItemAliasRequest> aliases,
			List<InventoryItemBarcodeRequest> barcodes) {
		item.getUnits().clear();
		for (InventoryItemUnitRequest unitRequest : units) {
			InventoryItemUnit unit = new InventoryItemUnit();
			unit.setInventoryItem(item);
			unit.setUnitCode(unitRequest.getUnitCode().trim());
			unit.setUnitName(unitRequest.getUnitName().trim());
			unit.setConversionFactor(unitRequest.getConversionFactor());
			unit.setBaseUnit(unitRequest.isBaseUnit());
			item.getUnits().add(unit);
		}

		item.getAliases().clear();
		if (aliases != null) {
			for (InventoryItemAliasRequest aliasRequest : aliases) {
				InventoryItemAlias alias = new InventoryItemAlias();
				alias.setInventoryItem(item);
				alias.setAlias(aliasRequest.getAlias().trim());
				item.getAliases().add(alias);
			}
		}

		item.getBarcodes().clear();
		if (barcodes != null) {
			for (InventoryItemBarcodeRequest barcodeRequest : barcodes) {
				InventoryItemBarcode barcode = new InventoryItemBarcode();
				barcode.setInventoryItem(item);
				barcode.setBarcode(barcodeRequest.getBarcode().trim());
				barcode.setUnitCode(barcodeRequest.getUnitCode() != null ? barcodeRequest.getUnitCode().trim() : null);
				item.getBarcodes().add(barcode);
			}
		}
	}
}
