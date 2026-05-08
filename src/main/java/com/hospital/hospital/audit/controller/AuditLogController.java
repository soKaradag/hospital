package com.hospital.hospital.audit.controller;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hospital.hospital.audit.dto.AuditLogResponse;
import com.hospital.hospital.audit.model.AuditStatus;
import com.hospital.hospital.audit.service.AuditLogQueryService;
import com.hospital.hospital.auth.annotation.RequirePermission;
import com.hospital.hospital.auth.model.PermissionCodes;
import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.common.dto.ApiResponse;
import com.hospital.hospital.common.dto.PageResponse;

@Validated
@RestController
@RequestMapping("/api/audit-logs")
@RequirePermission(PermissionCodes.AUDIT_LOGS_READ)
public class AuditLogController {

	private final AuditLogQueryService auditLogQueryService;

	public AuditLogController(AuditLogQueryService auditLogQueryService) {
		this.auditLogQueryService = auditLogQueryService;
	}

	@GetMapping
	public ApiResponse<PageResponse<AuditLogResponse>> getAll(
			@RequestParam(required = false) String action,
			@RequestParam(required = false) String entityName,
			@RequestParam(required = false) AuditStatus status,
			@RequestParam(required = false) UUID actorUserId,
			@RequestParam(required = false) Role actorRole,
			@RequestParam(required = false) String requestPath,
			@RequestParam(required = false) String httpMethod,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant occurredFrom,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant occurredTo,
			@PageableDefault(size = 20, sort = "occurredAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ApiResponse.success("Audit logs retrieved successfully",
				PageResponse.from(auditLogQueryService.getAll(
						action,
						entityName,
						status,
						actorUserId,
						actorRole,
						requestPath,
						httpMethod,
						occurredFrom,
						occurredTo,
						pageable)));
	}

	@GetMapping("/{id}")
	public ApiResponse<AuditLogResponse> getById(@PathVariable UUID id) {
		return ApiResponse.success("Audit log retrieved successfully", auditLogQueryService.getById(id));
	}
}
