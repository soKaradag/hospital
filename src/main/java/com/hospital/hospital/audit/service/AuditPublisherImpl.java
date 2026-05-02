package com.hospital.hospital.audit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hospital.hospital.audit.model.AuditEvent;
import com.hospital.hospital.audit.observer.AuditObserver;

/*
- Publisher, observer pattern içindeki merkez dağıtım noktasını temsil eder.
- Aspect audit event ürettiğinde, event burada tüm observer'lara fan-out edilir.
*/
@Service
public class AuditPublisherImpl implements AuditPublisher {

	private final List<AuditObserver> observers;

	public AuditPublisherImpl(List<AuditObserver> observers) {
		this.observers = observers;
	}

	@Override
	public void publish(AuditEvent event) {
		observers.forEach(observer -> observer.onAuditEvent(event));
	}
}
