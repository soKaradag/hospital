package com.hospital.hospital.common.exception;

// Tekil olması gereken bir kayıt tekrar oluşturulmak istendiğinde fırlatılır.
public class DuplicateResourceException extends RuntimeException {

	public DuplicateResourceException(String message) {
		super(message);
	}
}
