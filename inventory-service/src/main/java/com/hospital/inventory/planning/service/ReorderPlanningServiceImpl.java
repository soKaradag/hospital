package com.hospital.inventory.planning.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.inventory.common.exception.BusinessRuleViolationException;
import com.hospital.inventory.common.exception.DuplicateResourceException;
import com.hospital.inventory.common.exception.ResourceNotFoundException;
import com.hospital.inventory.inventoryitem.model.InventoryItem;
import com.hospital.inventory.inventoryitem.repository.InventoryItemRepository;
import com.hospital.inventory.planning.dto.CreateReorderRuleRequest;
import com.hospital.inventory.planning.dto.ReorderRecommendationResponse;
import com.hospital.inventory.planning.dto.ReorderRuleResponse;
import com.hospital.inventory.planning.model.ReorderRule;
import com.hospital.inventory.planning.repository.ReorderRuleRepository;
import com.hospital.inventory.stock.model.ReservationStatus;
import com.hospital.inventory.stock.repository.StockBatchRepository;
import com.hospital.inventory.stock.repository.StockReservationRepository;
import com.hospital.inventory.supplier.model.Supplier;
import com.hospital.inventory.supplier.repository.SupplierRepository;
import com.hospital.inventory.warehouse.model.Warehouse;
import com.hospital.inventory.warehouse.model.WarehouseZone;
import com.hospital.inventory.warehouse.repository.WarehouseRepository;
import com.hospital.inventory.warehouse.repository.WarehouseZoneRepository;

@Service
public class ReorderPlanningServiceImpl implements ReorderPlanningService {

	private final ReorderRuleRepository reorderRuleRepository;
	private final InventoryItemRepository inventoryItemRepository;
	private final WarehouseRepository warehouseRepository;
	private final WarehouseZoneRepository warehouseZoneRepository;
	private final SupplierRepository supplierRepository;
	private final StockBatchRepository stockBatchRepository;
	private final StockReservationRepository stockReservationRepository;

	public ReorderPlanningServiceImpl(
			ReorderRuleRepository reorderRuleRepository,
			InventoryItemRepository inventoryItemRepository,
			WarehouseRepository warehouseRepository,
			WarehouseZoneRepository warehouseZoneRepository,
			SupplierRepository supplierRepository,
			StockBatchRepository stockBatchRepository,
			StockReservationRepository stockReservationRepository) {
		this.reorderRuleRepository = reorderRuleRepository;
		this.inventoryItemRepository = inventoryItemRepository;
		this.warehouseRepository = warehouseRepository;
		this.warehouseZoneRepository = warehouseZoneRepository;
		this.supplierRepository = supplierRepository;
		this.stockBatchRepository = stockBatchRepository;
		this.stockReservationRepository = stockReservationRepository;
	}

	@Override
	@Transactional
	public ReorderRuleResponse createRule(CreateReorderRuleRequest request) {
		if (request.getTargetQuantity().compareTo(request.getMinQuantity()) < 0) {
			throw new BusinessRuleViolationException("targetQuantity must be greater than or equal to minQuantity");
		}

		InventoryItem inventoryItem = inventoryItemRepository.findByIdAndActiveTrue(request.getInventoryItemId())
				.orElseThrow(() -> new ResourceNotFoundException(
						"Inventory item not found: " + request.getInventoryItemId()));
		Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse not found: " + request.getWarehouseId()));
		WarehouseZone warehouseZone = getWarehouseZone(request.getWarehouseZoneId(), warehouse.getId());
		Supplier preferredSupplier = getSupplier(request.getPreferredSupplierId());

		if (reorderRuleRepository.existsForLocation(
				inventoryItem.getId(),
				warehouse.getId(),
				warehouseZone != null ? warehouseZone.getId() : null)) {
			throw new DuplicateResourceException("Reorder rule already exists for this item and location");
		}

		ReorderRule rule = new ReorderRule();
		rule.setInventoryItem(inventoryItem);
		rule.setWarehouse(warehouse);
		rule.setWarehouseZone(warehouseZone);
		rule.setPreferredSupplier(preferredSupplier);
		rule.setMinQuantity(request.getMinQuantity());
		rule.setTargetQuantity(request.getTargetQuantity());
		rule.setActive(request.isActive());
		return toResponse(reorderRuleRepository.save(rule));
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ReorderRuleResponse> getRules(Pageable pageable, boolean activeOnly) {
		Page<ReorderRule> rules = activeOnly
				? reorderRuleRepository.findAllByActiveTrue(pageable)
				: reorderRuleRepository.findAll(pageable);
		return rules.map(this::toResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ReorderRecommendationResponse> getRecommendations() {
		return reorderRuleRepository.findAllByActiveTrue().stream()
				.map(this::toRecommendation)
				.filter(recommendation -> recommendation.getAvailableQuantity()
						.compareTo(recommendation.getMinQuantity()) < 0)
				.toList();
	}

	@Override
	@Transactional
	public void deleteRule(UUID id) {
		ReorderRule rule = reorderRuleRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResourceNotFoundException("Reorder rule not found: " + id));
		rule.setActive(false);
		reorderRuleRepository.save(rule);
	}

	private WarehouseZone getWarehouseZone(UUID warehouseZoneId, UUID warehouseId) {
		if (warehouseZoneId == null) {
			return null;
		}
		WarehouseZone warehouseZone = warehouseZoneRepository.findById(warehouseZoneId)
				.orElseThrow(() -> new ResourceNotFoundException("Warehouse zone not found: " + warehouseZoneId));
		if (!warehouseZone.isActive()) {
			throw new ResourceNotFoundException("Warehouse zone not found: " + warehouseZoneId);
		}
		if (!warehouseZone.getWarehouse().getId().equals(warehouseId)) {
			throw new BusinessRuleViolationException("Warehouse zone does not belong to the selected warehouse");
		}
		return warehouseZone;
	}

	private Supplier getSupplier(UUID supplierId) {
		if (supplierId == null) {
			return null;
		}
		return supplierRepository.findByIdAndActiveTrue(supplierId)
				.orElseThrow(() -> new ResourceNotFoundException("Supplier not found: " + supplierId));
	}

	private ReorderRuleResponse toResponse(ReorderRule rule) {
		ReorderRuleResponse response = new ReorderRuleResponse();
		response.setId(rule.getId());
		response.setInventoryItemId(rule.getInventoryItem().getId());
		response.setWarehouseId(rule.getWarehouse().getId());
		response.setWarehouseZoneId(rule.getWarehouseZone() != null ? rule.getWarehouseZone().getId() : null);
		response.setPreferredSupplierId(
				rule.getPreferredSupplier() != null ? rule.getPreferredSupplier().getId() : null);
		response.setMinQuantity(rule.getMinQuantity());
		response.setTargetQuantity(rule.getTargetQuantity());
		response.setActive(rule.isActive());
		response.setCreatedAt(rule.getCreatedAt());
		response.setUpdatedAt(rule.getUpdatedAt());
		return response;
	}

	private ReorderRecommendationResponse toRecommendation(ReorderRule rule) {
		UUID warehouseZoneId = rule.getWarehouseZone() != null ? rule.getWarehouseZone().getId() : null;
		BigDecimal onHand = stockBatchRepository.sumQuantityOnHandByLocation(
				rule.getInventoryItem().getId(),
				rule.getWarehouse().getId(),
				warehouseZoneId);
		BigDecimal reserved = stockReservationRepository.sumQuantityByItemAndLocationAndStatus(
				rule.getInventoryItem().getId(),
				rule.getWarehouse().getId(),
				warehouseZoneId,
				ReservationStatus.ACTIVE);
		BigDecimal available = onHand.subtract(reserved);
		BigDecimal shortage = rule.getMinQuantity().subtract(available).max(BigDecimal.ZERO);
		BigDecimal suggested = rule.getTargetQuantity().subtract(available).max(BigDecimal.ZERO);

		ReorderRecommendationResponse response = new ReorderRecommendationResponse();
		response.setReorderRuleId(rule.getId());
		response.setInventoryItemId(rule.getInventoryItem().getId());
		response.setWarehouseId(rule.getWarehouse().getId());
		response.setWarehouseZoneId(warehouseZoneId);
		response.setPreferredSupplierId(
				rule.getPreferredSupplier() != null ? rule.getPreferredSupplier().getId() : null);
		response.setMinQuantity(rule.getMinQuantity());
		response.setTargetQuantity(rule.getTargetQuantity());
		response.setOnHandQuantity(onHand);
		response.setReservedQuantity(reserved);
		response.setAvailableQuantity(available);
		response.setShortageQuantity(shortage);
		response.setSuggestedOrderQuantity(suggested);
		return response;
	}
}
