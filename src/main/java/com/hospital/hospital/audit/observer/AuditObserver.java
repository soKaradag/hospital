package com.hospital.hospital.audit.observer;

import com.hospital.hospital.audit.model.AuditEvent;

public interface AuditObserver {

	// Publisher tarafından yayımlanan audit event'lerini işleyen gözlemci sözleşmesidir.
	void onAuditEvent(AuditEvent event);
}
