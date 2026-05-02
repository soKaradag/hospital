package com.hospital.hospital.surgery.controller;

import java.util.UUID;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital.auth.annotation.RequirePermission;
import com.hospital.hospital.auth.model.PermissionCodes;
import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.surgery.dto.CreateOperatingRoomRequest;
import com.hospital.hospital.surgery.dto.CreateSurgeryRequestRequest;
import com.hospital.hospital.surgery.dto.OperatingRoomResponse;
import com.hospital.hospital.surgery.dto.ScheduleSurgeryRequest;
import com.hospital.hospital.surgery.dto.SurgeryRequestResponse;
import com.hospital.hospital.surgery.dto.SurgeryResponse;
import com.hospital.hospital.surgery.service.SurgeryService;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/surgeries")
@RequirePermission(PermissionCodes.SURGERIES_READ)
public class SurgeryController {

	private final SurgeryService surgeryService;

	public SurgeryController(SurgeryService surgeryService) {
		this.surgeryService = surgeryService;
	}

	@PostMapping("/operating-rooms")
	@RequirePermission(PermissionCodes.SURGERIES_WRITE)
	public ApiResponse<OperatingRoomResponse> createOperatingRoom(@Valid @RequestBody CreateOperatingRoomRequest request) {
		return ApiResponse.success("Operating room created successfully", surgeryService.createOperatingRoom(request));
	}

	@PostMapping("/requests")
	@RequirePermission(PermissionCodes.SURGERIES_WRITE)
	public ApiResponse<SurgeryRequestResponse> createSurgeryRequest(
			@Valid @RequestBody CreateSurgeryRequestRequest request) {
		return ApiResponse.success("Surgery request created successfully", surgeryService.createSurgeryRequest(request));
	}

	@PostMapping
	@RequirePermission(PermissionCodes.SURGERIES_WRITE)
	public ApiResponse<SurgeryResponse> scheduleSurgery(@Valid @RequestBody ScheduleSurgeryRequest request) {
		return ApiResponse.success("Surgery scheduled successfully", surgeryService.scheduleSurgery(request));
	}

	@GetMapping("/{id}")
	public ApiResponse<SurgeryResponse> getSurgeryById(@PathVariable UUID id) {
		return ApiResponse.success("Surgery retrieved successfully", surgeryService.getSurgeryById(id));
	}
}
