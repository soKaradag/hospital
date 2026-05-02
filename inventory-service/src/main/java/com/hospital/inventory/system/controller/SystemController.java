package com.hospital.inventory.system.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.inventory.common.dto.ApiResponse;

@RestController
@RequestMapping("/api/inventory/system")
public class SystemController {

	@GetMapping("/health")
	public ApiResponse<Map<String, String>> health() {
		return ApiResponse.success(
				"Inventory service is healthy",
				Map.of("service", "inventory-service", "status", "UP"));
	}
}
