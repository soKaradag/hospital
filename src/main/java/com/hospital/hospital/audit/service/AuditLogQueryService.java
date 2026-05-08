package com.hospital.hospital.audit.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hospital.hospital.audit.dto.AuditLogResponse;
import com.hospital.hospital.audit.model.AuditStatus;
import com.hospital.hospital.auth.model.Role;

public interface AuditLogQueryService {

	Page<AuditLogResponse> getAll(
			String action,
			String entityName,
			AuditStatus status,
			UUID actorUserId,
			Role actorRole,
			String requestPath,
			String httpMethod,
			Instant occurredFrom,
			Instant occurredTo,
			Pageable pageable);

	AuditLogResponse getById(UUID id);
}
