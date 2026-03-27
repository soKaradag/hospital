package com.hospital.hospital.audit.service;

import com.hospital.hospital.audit.model.AuditEvent;

public interface AuditPublisher {

	// Üretilen audit event'ini tüm observer'lara iletir.
	void publish(AuditEvent event);
}
