package com.hospital.hospital.audit.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hospital.hospital.audit.model.AuditEvent;

/*
- Bu observer, audit event'lerini şimdilik uygulama loguna yazar.
- Sonraki adımda veritabanı kalıcılığı eklendiğinde aynı publisher üzerinden ikinci bir observer daha bağlanacaktır.
*/
@Component
public class LoggingAuditObserver implements AuditObserver {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAuditObserver.class);

	@Override
	public void onAuditEvent(AuditEvent event) {
		LOGGER.info(
				"Audit event action={} entity={} status={} actorUserId={} path={} method={} message={}",
				event.action(),
				event.entityName(),
				event.status(),
				event.actorUserId(),
				event.requestPath(),
				event.httpMethod(),
				event.message());
	}
}
