package com.hospital.hospital.audit.observer;

import org.springframework.stereotype.Component;

import com.hospital.hospital.audit.model.AuditEvent;
import com.hospital.hospital.audit.model.AuditLog;
import com.hospital.hospital.audit.repository.AuditLogRepository;

/*
- Bu observer, yayımlanan audit event'lerini veritabanına kaydeder.
- Böylece observer pattern korunurken log ve kalıcılık davranışları birbirinden ayrılmış olur.
*/
@Component
public class PersistentAuditObserver implements AuditObserver {

	private final AuditLogRepository auditLogRepository;

	public PersistentAuditObserver(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
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
		auditLogRepository.save(auditLog);
	}
}
