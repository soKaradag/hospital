package com.hospital.inventory.inventorycategory.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.DuplicateResourceException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventorycategory.dto.CreateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.dto.InventoryCategoryResponse;
import com.hospital.inventory.inventorycategory.dto.UpdateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.mapper.InventoryCategoryMapper;
import com.hospital.inventory.inventorycategory.model.InventoryCategory;
import com.hospital.inventory.inventorycategory.repository.InventoryCategoryRepository;

@Service
public class InventoryCategoryServiceImpl implements InventoryCategoryService {

	private final InventoryCategoryRepository inventoryCategoryRepository;
	private final InventoryCategoryMapper inventoryCategoryMapper;

	public InventoryCategoryServiceImpl(
			InventoryCategoryRepository inventoryCategoryRepository,
			InventoryCategoryMapper inventoryCategoryMapper) {
		this.inventoryCategoryRepository = inventoryCategoryRepository;
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
		return inventoryCategoryRepository.findAll(pageable).map(inventoryCategoryMapper::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<InventoryCategoryResponse> search(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return getAll(pageable);
		}
		String trimmedKeyword = keyword.trim();
		return inventoryCategoryRepository.findAllByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
				trimmedKeyword,
				trimmedKeyword,
				pageable).map(inventoryCategoryMapper::toResponse);
	}

	private InventoryCategory getCategory(UUID id) {
		return inventoryCategoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Inventory category not found: " + id));
	}
}
