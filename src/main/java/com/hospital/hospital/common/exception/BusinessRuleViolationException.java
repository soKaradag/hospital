package com.hospital.hospital.common.exception;

// Domain kuralları ihlal edildiğinde fırlatılır.
public class BusinessRuleViolationException extends RuntimeException {

	public BusinessRuleViolationException(String message) {
		super(message);
	}
}
