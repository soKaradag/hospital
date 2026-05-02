package com.hospital.inventory.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hospital.inventory.common.dto.ApiErrorResponse;
import com.hospital.inventory.common.dto.ValidationErrorDetail;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
		List<ValidationErrorDetail> errors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(this::toValidationError)
				.toList();
		return ResponseEntity.badRequest()
				.body(ApiErrorResponse.of(ErrorCode.VALIDATION_ERROR, "Request validation failed", errors));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, exception.getMessage(), List.of()));
	}

	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateResourceException(DuplicateResourceException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiErrorResponse.of(ErrorCode.DUPLICATE_RESOURCE, exception.getMessage(), List.of()));
	}

	@ExceptionHandler(BusinessRuleViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleBusinessRuleViolationException(
			BusinessRuleViolationException exception) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
				.body(ApiErrorResponse.of(ErrorCode.BUSINESS_RULE_VIOLATION, exception.getMessage(), List.of()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiErrorResponse> handleUnauthorizedException(UnauthorizedException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiErrorResponse.of(ErrorCode.UNAUTHORIZED, exception.getMessage(), List.of()));
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ApiErrorResponse> handleForbiddenException(ForbiddenException exception) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiErrorResponse.of(ErrorCode.FORBIDDEN, exception.getMessage(), List.of()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiErrorResponse.of(
						ErrorCode.INTERNAL_SERVER_ERROR,
						exception.getMessage() != null ? exception.getMessage() : "Unexpected error",
						List.of()));
	}

	private ValidationErrorDetail toValidationError(FieldError error) {
		return new ValidationErrorDetail(error.getField(), error.getDefaultMessage());
	}
}
