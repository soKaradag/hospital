package com.hospital.inventory.auth.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.auth.annotation.RequirePermission;
import com.hospital.inventory.auth.context.CurrentUserContext;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/inventory/system")
public class SystemSessionController {

	private final CurrentUserContext currentUserContext;

	public SystemSessionController(CurrentUserContext currentUserContext) {
		this.currentUserContext = currentUserContext;
	}

	@GetMapping("/session")
	@RequirePermission(PermissionCodes.INVENTORY_ITEMS_READ)
	public ApiResponse<Object> session() {
		return ApiResponse.success("Inventory session resolved successfully", currentUserContext.getUser());
	}
}
