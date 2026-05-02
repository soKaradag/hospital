package com.hospital.inventory.planning.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.common.dto.PageResponse;
import com.hospital.inventory.planning.dto.CreateReorderRuleRequest;
import com.hospital.inventory.planning.dto.ReorderRecommendationResponse;
import com.hospital.inventory.planning.dto.ReorderRuleResponse;
import com.hospital.inventory.planning.service.ReorderPlanningService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/inventory")
public class ReorderPlanningController {

	private final ReorderPlanningService reorderPlanningService;

	public ReorderPlanningController(ReorderPlanningService reorderPlanningService) {
		this.reorderPlanningService = reorderPlanningService;
	}

	@PostMapping("/reorder-rules")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_WRITE)
	public ApiResponse<ReorderRuleResponse> createRule(@Valid @RequestBody CreateReorderRuleRequest request) {
		return ApiResponse.success(
				"Reorder rule created successfully",
				reorderPlanningService.createRule(request));
	}

	@GetMapping("/reorder-rules")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_READ)
	public ApiResponse<PageResponse<ReorderRuleResponse>> getRules(
			@RequestParam(defaultValue = "true") boolean activeOnly,
			@PageableDefault(size = 20) Pageable pageable) {
		return ApiResponse.success(
				"Reorder rules retrieved successfully",
				PageResponse.from(reorderPlanningService.getRules(pageable, activeOnly)));
	}

	@GetMapping("/reorder-recommendations")
	@RequirePermission(PermissionCodes.INVENTORY_PURCHASE_READ)
	public ApiResponse<List<ReorderRecommendationResponse>> getRecommendations() {
		return ApiResponse.success(
				"Reorder recommendations retrieved successfully",
				reorderPlanningService.getRecommendations());
	}
}
