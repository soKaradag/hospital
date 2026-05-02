package com.hospital.hospital.inventory.exception;

public class InventoryShortageException extends RuntimeException {

	public InventoryShortageException(String message) {
		super(message);
	}
}
