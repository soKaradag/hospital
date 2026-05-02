package com.hospital.inventory.inventorycategory.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.inventory.inventorycategory.dto.CreateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.dto.InventoryCategoryResponse;
import com.hospital.inventory.inventorycategory.dto.UpdateInventoryCategoryRequest;

public interface InventoryCategoryService {

	InventoryCategoryResponse create(CreateInventoryCategoryRequest request);

	InventoryCategoryResponse update(UUID id, UpdateInventoryCategoryRequest request);

	InventoryCategoryResponse getById(UUID id);

	Page<InventoryCategoryResponse> getAll(Pageable pageable);

	Page<InventoryCategoryResponse> search(String keyword, Pageable pageable);
}
