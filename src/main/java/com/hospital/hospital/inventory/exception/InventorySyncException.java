package com.hospital.hospital.inventory.exception;

public class InventorySyncException extends RuntimeException {

	public InventorySyncException(String message) {
		super(message);
	}

	public InventorySyncException(String message, Throwable cause) {
		super(message, cause);
	}
}
