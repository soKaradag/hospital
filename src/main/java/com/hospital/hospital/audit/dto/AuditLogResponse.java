package com.hospital.hospital.audit.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.hospital.hospital.audit.model.AuditStatus;
import com.hospital.hospital.auth.model.Role;

public class AuditLogResponse {

	private UUID id;
	private String action;
	private String entityName;
	private String description;
	private AuditStatus status;
	private String message;
	private String errorCode;
	private UUID actorUserId;
	private Role actorRole;
	private String requestPath;
	private String httpMethod;
	private Instant occurredAt;
	private Instant createdAt;
	private Instant updatedAt;
	private List<AuditLogDetailResponse> details = new ArrayList<>();

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AuditStatus getStatus() {
		return status;
	}

	public void setStatus(AuditStatus status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public UUID getActorUserId() {
		return actorUserId;
	}

	public void setActorUserId(UUID actorUserId) {
		this.actorUserId = actorUserId;
	}

	public Role getActorRole() {
		return actorRole;
	}

	public void setActorRole(Role actorRole) {
		this.actorRole = actorRole;
	}

	public String getRequestPath() {
		return requestPath;
	}

	public void setRequestPath(String requestPath) {
		this.requestPath = requestPath;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}

	public Instant getOccurredAt() {
		return occurredAt;
	}

	public void setOccurredAt(Instant occurredAt) {
		this.occurredAt = occurredAt;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}

	public List<AuditLogDetailResponse> getDetails() {
		return details;
	}

	public void setDetails(List<AuditLogDetailResponse> details) {
		this.details = details;
	}
}
