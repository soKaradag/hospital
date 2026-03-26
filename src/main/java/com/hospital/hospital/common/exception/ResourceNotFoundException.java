package com.hospital.hospital.common.exception;

// İstenen kaynak veritabanında bulunamadığında fırlatılır.
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
