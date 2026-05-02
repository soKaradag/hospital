package com.hospital.inventory.inventoryitem.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.inventory.inventoryitem.dto.CreateInventoryItemRequest;
import com.hospital.inventory.inventoryitem.dto.InventoryItemResponse;
import com.hospital.inventory.inventoryitem.dto.UpdateInventoryItemRequest;

public interface InventoryItemService {

	InventoryItemResponse create(CreateInventoryItemRequest request);

	InventoryItemResponse update(UUID id, UpdateInventoryItemRequest request);

	InventoryItemResponse getById(UUID id);

	Page<InventoryItemResponse> getAll(Pageable pageable);

	Page<InventoryItemResponse> search(String keyword, Pageable pageable);
}
