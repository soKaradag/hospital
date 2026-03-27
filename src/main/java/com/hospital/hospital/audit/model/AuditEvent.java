package com.hospital.hospital.audit.model;

import java.time.Instant;
import java.util.UUID;

import com.hospital.hospital.auth.model.Role;

/*
- Audit event, iş akışı tamamlandığında observer katmanına gönderilen sade veri nesnesidir.
- Bu aşamada henüz veritabanı modeli değil, uygulama içi event modeli olarak kullanılır.
*/
public record AuditEvent(
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
}
