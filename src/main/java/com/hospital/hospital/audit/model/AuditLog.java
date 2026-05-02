package com.hospital.hospital.audit.model;

import java.time.Instant;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.hospital.hospital.auth.model.Role;
import com.hospital.hospital.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

/*
- Bu entity, audit event'lerinin veritabanındaki kalıcı karşılığıdır.
- AuditEvent uygulama içi event modeli iken AuditLog kalıcı kayıt modelidir.
*/
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

	public AuditLog() {
	}

	public AuditLog(
			String action,
			String entityName,
			String description,
			AuditStatus status,
			String message,
			String errorCode,
			UUID actorUserId,
			Role actorRole,
			String requestPath,
			String httpMethod,
			Instant occurredAt) {
		this.action = action;
		this.entityName = entityName;
		this.description = description;
		this.status = status;
		this.message = message;
		this.errorCode = errorCode;
		this.actorUserId = actorUserId;
		this.actorRole = actorRole;
		this.requestPath = requestPath;
		this.httpMethod = httpMethod;
		this.occurredAt = occurredAt;
	}

	@Column(name = "action", nullable = false, length = 100)
	private String action;

	@Column(name = "entity_name", nullable = false, length = 100)
	private String entityName;

	@Column(name = "description", length = 255)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 40)
	private AuditStatus status;

	@Column(name = "message", length = 500)
	private String message;

	@Column(name = "error_code", length = 50)
	private String errorCode;

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "actor_user_id", length = 36)
	private UUID actorUserId;

	@Enumerated(EnumType.STRING)
	@Column(name = "actor_role", length = 30)
	private Role actorRole;

	@Column(name = "request_path", length = 255)
	private String requestPath;

	@Column(name = "http_method", length = 10)
	private String httpMethod;

	@Column(name = "occurred_at", nullable = false)
	private Instant occurredAt;

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
}
