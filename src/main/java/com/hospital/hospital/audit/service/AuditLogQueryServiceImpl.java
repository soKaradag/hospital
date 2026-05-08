package com.hospital.hospital.audit.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.hospital.audit.dto.AuditLogDetailResponse;
import com.hospital.hospital.audit.dto.AuditLogResponse;
import com.hospital.hospital.audit.model.AuditLog;
import com.hospital.hospital.audit.model.AuditLogDetail;
import com.hospital.hospital.audit.model.AuditStatus;
import com.hospital.hospital.audit.repository.AuditLogDetailRepository;
import com.hospital.hospital.audit.repository.AuditLogRepository;
import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.common.exception.ResourceNotFoundException;

@Service
public class AuditLogQueryServiceImpl implements AuditLogQueryService {

	private final AuditLogRepository auditLogRepository;
	private final AuditLogDetailRepository auditLogDetailRepository;

	public AuditLogQueryServiceImpl(
			AuditLogRepository auditLogRepository,
			AuditLogDetailRepository auditLogDetailRepository) {
		this.auditLogRepository = auditLogRepository;
		this.auditLogDetailRepository = auditLogDetailRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<AuditLogResponse> getAll(
			String action,
			String entityName,
			AuditStatus status,
			UUID actorUserId,
			Role actorRole,
			String requestPath,
			String httpMethod,
			Instant occurredFrom,
			Instant occurredTo,
			Pageable pageable) {
		Specification<AuditLog> specification = Specification
				.where(equalsIgnoreCase("action", action))
				.and(equalsIgnoreCase("entityName", entityName))
				.and(equalsValue("status", status))
				.and(equalsValue("actorUserId", actorUserId))
				.and(equalsValue("actorRole", actorRole))
				.and(containsIgnoreCase("requestPath", requestPath))
				.and(equalsIgnoreCase("httpMethod", httpMethod))
				.and(occurredAtFrom(occurredFrom))
				.and(occurredAtTo(occurredTo));
		return auditLogRepository.findAll(specification, pageable).map(this::toSummaryResponse);
	}

	@Override
	@Transactional(readOnly = true)
	public AuditLogResponse getById(UUID id) {
		AuditLog auditLog = auditLogRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Audit log not found: " + id));
		AuditLogResponse response = toSummaryResponse(auditLog);
		response.setDetails(auditLogDetailRepository.findAllByAuditLogIdOrderByDetailKeyAsc(id)
				.stream()
				.map(this::toDetailResponse)
				.toList());
		return response;
	}

	private AuditLogResponse toSummaryResponse(AuditLog auditLog) {
		AuditLogResponse response = new AuditLogResponse();
		response.setId(auditLog.getId());
		response.setAction(auditLog.getAction());
		response.setEntityName(auditLog.getEntityName());
		response.setDescription(auditLog.getDescription());
		response.setStatus(auditLog.getStatus());
		response.setMessage(auditLog.getMessage());
		response.setErrorCode(auditLog.getErrorCode());
		response.setActorUserId(auditLog.getActorUserId());
		response.setActorRole(auditLog.getActorRole());
		response.setRequestPath(auditLog.getRequestPath());
		response.setHttpMethod(auditLog.getHttpMethod());
		response.setOccurredAt(auditLog.getOccurredAt());
		response.setCreatedAt(auditLog.getCreatedAt());
		response.setUpdatedAt(auditLog.getUpdatedAt());
		return response;
	}

	private AuditLogDetailResponse toDetailResponse(AuditLogDetail detail) {
		AuditLogDetailResponse response = new AuditLogDetailResponse();
		response.setId(detail.getId());
		response.setKey(detail.getDetailKey());
		response.setValue(detail.getDetailValue());
		return response;
	}

	private Specification<AuditLog> equalsIgnoreCase(String fieldName, String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String normalized = value.trim().toLowerCase();
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(
				criteriaBuilder.lower(root.get(fieldName)),
				normalized);
	}

	private Specification<AuditLog> containsIgnoreCase(String fieldName, String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		String normalized = "%" + value.trim().toLowerCase() + "%";
		return (root, query, criteriaBuilder) -> criteriaBuilder.like(
				criteriaBuilder.lower(root.get(fieldName)),
				normalized);
	}

	private <T> Specification<AuditLog> equalsValue(String fieldName, T value) {
		if (value == null) {
			return null;
		}
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(fieldName), value);
	}

	private Specification<AuditLog> occurredAtFrom(Instant value) {
		if (value == null) {
			return null;
		}
		return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("occurredAt"), value);
	}

	private Specification<AuditLog> occurredAtTo(Instant value) {
		if (value == null) {
			return null;
		}
		return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("occurredAt"), value);
	}
}
