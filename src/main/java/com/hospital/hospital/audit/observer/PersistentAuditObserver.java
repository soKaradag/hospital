package com.hospital.hospital.audit.observer;

import org.springframework.stereotype.Component;

import com.hospital.hospital.audit.model.AuditEvent;
import com.hospital.hospital.audit.model.AuditLog;
import com.hospital.hospital.audit.model.AuditLogDetail;
import com.hospital.hospital.audit.repository.AuditLogDetailRepository;
import com.hospital.hospital.audit.repository.AuditLogRepository;

/*
- Bu observer, yayımlanan audit event'lerini veritabanına kaydeder.
- Böylece observer pattern korunurken log ve kalıcılık davranışları birbirinden ayrılmış olur.
*/
@Component
public class PersistentAuditObserver implements AuditObserver {

	private final AuditLogRepository auditLogRepository;
	private final AuditLogDetailRepository auditLogDetailRepository;

	public PersistentAuditObserver(AuditLogRepository auditLogRepository, AuditLogDetailRepository auditLogDetailRepository) {
		this.auditLogRepository = auditLogRepository;
		this.auditLogDetailRepository = auditLogDetailRepository;
	}

	@Override
	// Event'i veritabanına kaydeder.
	public void onAuditEvent(AuditEvent event) {
		AuditLog auditLog = new AuditLog(
				event.action(),
				event.entityName(),
				event.description(),
				event.status(),
				event.message(),
				event.errorCode(),
				event.actorUserId(),
				event.actorRole(),
				event.requestPath(),
				event.httpMethod(),
				event.occurredAt());
		AuditLog savedAuditLog = auditLogRepository.save(auditLog);
		saveDetail(savedAuditLog, "request_path", event.requestPath());
		saveDetail(savedAuditLog, "http_method", event.httpMethod());
		saveDetail(savedAuditLog, "message", event.message());
		saveDetail(savedAuditLog, "error_code", event.errorCode());
		saveDetail(savedAuditLog, "actor_role", event.actorRole() != null ? event.actorRole().name() : null);
	}

	private void saveDetail(AuditLog auditLog, String key, String value) {
		if (value == null || value.isBlank()) {
			return;
		}
		AuditLogDetail detail = new AuditLogDetail();
		detail.setAuditLog(auditLog);
		detail.setDetailKey(key);
		detail.setDetailValue(value);
		auditLogDetailRepository.save(detail);
	}
}
