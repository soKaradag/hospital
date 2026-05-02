package com.hospital.inventory.inventoryitem.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.hospital.inventory.inventoryitem.dto.CreateInventoryItemRequest;
import com.hospital.inventory.inventoryitem.dto.InventoryItemAliasResponse;
import com.hospital.inventory.inventoryitem.dto.InventoryItemBarcodeResponse;
import com.hospital.inventory.inventoryitem.dto.InventoryItemResponse;
import com.hospital.inventory.inventoryitem.dto.InventoryItemUnitResponse;
import com.hospital.inventory.inventoryitem.dto.UpdateInventoryItemRequest;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.model.InventoryItemAlias;
import com.hospital.inventory.inventoryitem.model.InventoryItemBarcode;
import com.hospital.inventory.inventoryitem.model.InventoryItemUnit;

@Component
public class InventoryItemMapper {

	public InventoryItem toEntity(CreateInventoryItemRequest request) {
		InventoryItem item = new InventoryItem();
		updateEntity(item, request.getCode(), request.getName(), request.getDescription(), request.isTrackBatches(),
				request.isTrackExpiry(), request.isActive());
		return item;
	}

	public void updateEntity(UpdateInventoryItemRequest request, InventoryItem item) {
		updateEntity(item, request.getCode(), request.getName(), request.getDescription(), request.isTrackBatches(),
				request.isTrackExpiry(), request.isActive());
	}

	private void updateEntity(
			InventoryItem item,
			String code,
			String name,
			String description,
			boolean trackBatches,
			boolean trackExpiry,
			boolean active) {
		item.setCode(code.trim());
		item.setName(name.trim());
		item.setDescription(description != null ? description.trim() : null);
		item.setTrackBatches(trackBatches);
		item.setTrackExpiry(trackExpiry);
		item.setActive(active);
	}

	public InventoryItemResponse toResponse(InventoryItem item) {
		InventoryItemResponse response = new InventoryItemResponse();
		response.setId(item.getId());
		response.setCode(item.getCode());
		response.setName(item.getName());
		response.setDescription(item.getDescription());
		response.setTrackBatches(item.isTrackBatches());
		response.setTrackExpiry(item.isTrackExpiry());
		response.setActive(item.isActive());
		response.setCategoryId(item.getCategory().getId());
		response.setCategoryCode(item.getCategory().getCode());
		response.setCategoryName(item.getCategory().getName());
		response.setUnits(item.getUnits().stream().map(this::toResponse).toList());
		response.setAliases(item.getAliases().stream().map(this::toResponse).toList());
		response.setBarcodes(item.getBarcodes().stream().map(this::toResponse).toList());
		response.setCreatedAt(item.getCreatedAt());
		response.setUpdatedAt(item.getUpdatedAt());
		return response;
	}

	private InventoryItemUnitResponse toResponse(InventoryItemUnit unit) {
		InventoryItemUnitResponse response = new InventoryItemUnitResponse();
		response.setId(unit.getId());
		response.setUnitCode(unit.getUnitCode());
		response.setUnitName(unit.getUnitName());
		response.setConversionFactor(unit.getConversionFactor());
		response.setBaseUnit(unit.isBaseUnit());
		return response;
	}

	private InventoryItemAliasResponse toResponse(InventoryItemAlias alias) {
		InventoryItemAliasResponse response = new InventoryItemAliasResponse();
		response.setId(alias.getId());
		response.setAlias(alias.getAlias());
		return response;
	}

	private InventoryItemBarcodeResponse toResponse(InventoryItemBarcode barcode) {
		InventoryItemBarcodeResponse response = new InventoryItemBarcodeResponse();
		response.setId(barcode.getId());
		response.setBarcode(barcode.getBarcode());
		response.setUnitCode(barcode.getUnitCode());
		return response;
	}
}
