package com.hospital.hospital.common.dto;

import java.time.Instant;
import java.util.List;

import com.hospital.hospital.common.exception.ErrorCode;

// Tüm hata cevaplarını tek formatta toplamak için kullanılır.
public class ApiErrorResponse {

	private boolean success;
	private ErrorCode code;
	private String message;
	private List<ValidationErrorDetail> errors;
	private Instant timestamp;

	public ApiErrorResponse() {
	}

	public ApiErrorResponse(boolean success, ErrorCode code, String message, List<ValidationErrorDetail> errors,
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

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public ErrorCode getCode() {
		return code;
	}

	public void setCode(ErrorCode code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<ValidationErrorDetail> getErrors() {
		return errors;
	}

	public void setErrors(List<ValidationErrorDetail> errors) {
		this.errors = errors;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}
}