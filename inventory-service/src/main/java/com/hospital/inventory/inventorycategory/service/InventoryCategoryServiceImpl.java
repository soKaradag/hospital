package com.hospital.inventory.inventorycategory.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.DuplicateResourceException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventorycategory.dto.CreateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.dto.InventoryCategoryResponse;
import com.hospital.inventory.inventorycategory.dto.UpdateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.mapper.InventoryCategoryMapper;
import com.hospital.inventory.inventorycategory.model.InventoryCategory;
import com.hospital.inventory.inventorycategory.repository.InventoryCategoryRepository;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;

@Service
public class InventoryCategoryServiceImpl implements InventoryCategoryService {

	private final InventoryCategoryRepository inventoryCategoryRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final InventoryCategoryMapper inventoryCategoryMapper;

	public InventoryCategoryServiceImpl(
			InventoryCategoryRepository inventoryCategoryRepository,
			InventoryItemRepository inventoryItemRepository,
			InventoryCategoryMapper inventoryCategoryMapper) {
		this.inventoryCategoryRepository = inventoryCategoryRepository;
		this.inventoryItemRepository = inventoryItemRepository;
		this.inventoryCategoryMapper = inventoryCategoryMapper;
	}

	@Override
	@Transactional
	public InventoryCategoryResponse create(CreateInventoryCategoryRequest request) {
		if (inventoryCategoryRepository.existsByCodeIgnoreCase(request.getCode().trim())) {
			throw new DuplicateResourceException("Inventory category code already exists: " + request.getCode());
		}
		return inventoryCategoryMapper.toResponse(
				inventoryCategoryRepository.save(inventoryCategoryMapper.toEntity(request)));
	}

	@Override
	@Transactional
	public InventoryCategoryResponse update(UUID id, UpdateInventoryCategoryRequest request) {
		InventoryCategory category = getCategory(id);
		inventoryCategoryRepository.findByCodeIgnoreCase(request.getCode().trim())
				.filter(existing -> !existing.getId().equals(id))
				.ifPresent(existing -> {
					throw new DuplicateResourceException("Inventory category code already exists: " + request.getCode());
				});
		inventoryCategoryMapper.updateEntity(request, category);
		return inventoryCategoryMapper.toResponse(inventoryCategoryRepository.save(category));
	}

	@Override
	@Transactional(readOnly = true)
	public InventoryCategoryResponse getById(UUID id) {
		return inventoryCategoryMapper.toResponse(getCategory(id));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryCategoryResponse> getAll(Pageable pageable) {
		return inventoryCategoryRepository.findAllByActiveTrue(pageable).map(inventoryCategoryMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryCategoryResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String trimmedKeyword = keyword.trim();
		return inventoryCategoryRepository.findAllByActiveTrueAndNameContainingIgnoreCaseOrActiveTrueAndCodeContainingIgnoreCase(
				trimmedKeyword,
				trimmedKeyword,
				pageable).map(inventoryCategoryMapper::toResponse);
	}

	@Override
	@Transactional
	public void delete(UUID id) {
		InventoryCategory category = getCategory(id);
		if (inventoryItemRepository.countByCategoryIdAndActiveTrue(id) > 0) {
			throw new BusinessRuleViolationException("Inventory category with active items cannot be deleted");
		}
		category.setActive(false);
		inventoryCategoryRepository.save(category);
	}

	private InventoryCategory getCategory(UUID id) {
		return inventoryCategoryRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory category not found: " + id));
	}
}
