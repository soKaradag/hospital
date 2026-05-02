package com.hospital.inventory.inventorycategory.mapper;

import org.springframework.stereotype.Component;

import com.hospital.inventory.inventorycategory.dto.CreateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.dto.InventoryCategoryResponse;
import com.hospital.inventory.inventorycategory.dto.UpdateInventoryCategoryRequest;
import com.hospital.inventory.inventorycategory.model.InventoryCategory;

@Component
public class InventoryCategoryMapper {

	public InventoryCategory toEntity(CreateInventoryCategoryRequest request) {
		InventoryCategory category = new InventoryCategory();
		updateEntity(request.getCode(), request.getName(), request.getDescription(), request.isActive(), category);
		return category;
	}

	public void updateEntity(UpdateInventoryCategoryRequest request, InventoryCategory category) {
		updateEntity(request.getCode(), request.getName(), request.getDescription(), request.isActive(), category);
	}

	private void updateEntity(
			String code,
			String name,
			String description,
			boolean active,
			InventoryCategory category) {
		category.setCode(code.trim());
		category.setName(name.trim());
		category.setDescription(description != null ? description.trim() : null);
		category.setActive(active);
	}

	public InventoryCategoryResponse toResponse(InventoryCategory category) {
		InventoryCategoryResponse response = new InventoryCategoryResponse();
		response.setId(category.getId());
		response.setCode(category.getCode());
		response.setName(category.getName());
		response.setDescription(category.getDescription());
		response.setActive(category.isActive());
		response.setCreatedAt(category.getCreatedAt());
		response.setUpdatedAt(category.getUpdatedAt());
		return response;
	}
}
