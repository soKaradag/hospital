package com.hospital.inventory.common.dto;

import java.time.Instant;
import java.util.List;

import com.hospital.inventory.common.exception.ErrorCode;

public class ApiErrorResponse {

	private final boolean success;
	private final ErrorCode code;
	private final String message;
	private final List<ValidationErrorDetail> errors;
	private final Instant timestamp;

	private ApiErrorResponse(
			boolean success,
			ErrorCode code,
			String message,
			List<ValidationErrorDetail> errors,
			Instant timestamp) {
		this.success = success;
		this.code = code;
		this.message = message;
		this.errors = errors;
		this.timestamp = timestamp;
	}

	public static ApiErrorResponse of(ErrorCode code, String message, List<ValidationErrorDetail> errors) {
		return new ApiErrorResponse(false, code, message, errors, Instant.now());
	}

	public boolean isSuccess() {
		return success;
	}

	public ErrorCode getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public List<ValidationErrorDetail> getErrors() {
		return errors;
	}

	public Instant getTimestamp() {
		return timestamp;
	}
}
