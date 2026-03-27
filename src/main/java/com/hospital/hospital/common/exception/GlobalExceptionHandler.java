package com.hospital.hospital.common.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hospital.hospital.common.dto.ApiErrorResponse;
import com.hospital.hospital.common.dto.ValidationErrorDetail;

/*
- Bu sınıf, uygulama genelinde fırlatılan hataları tek noktada yakalamak için kullanılır.
- @RestControllerAdvice sayesinde tüm controller'lar için ortak exception yakalama davranışı sağlar.
- Amaç, farklı controller'larda tekrar tekrar try-catch yazmak yerine hata yönetimini merkezileştirmektir.
- Burada yakalanan hatalar ortak API standardına uygun şekilde ApiErrorResponse olarak döndürülür.
- Böylece istemci tarafı her hata türünde aynı JSON yapısını alır.
- Validation, resource not found, duplicate resource, business rule ve beklenmeyen server hataları ayrı ayrı ele alınır.
- ResponseEntity kullanımı ile her hata türü için uygun HTTP status kodu döndürülür.
*/
@RestControllerAdvice
public class GlobalExceptionHandler {

	/*
	- MethodArgumentNotValidException, @Valid ile doğrulanan request body geçersiz olduğunda oluşur.
	- Bu handler field bazlı validation hatalarını toplayıp errors listesi halinde istemciye döndürür.
	- HTTP 400 Bad Request dönülür çünkü istek biçimsel veya kuralsal olarak geçersizdir.
	*/
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
		List<ValidationErrorDetail> errors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(this::toValidationErrorDetail)
				.toList();

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiErrorResponse.of(ErrorCode.VALIDATION_ERROR, "Request validation failed", errors));
	}

	/*
	- ResourceNotFoundException, istenen kayıt veritabanında bulunamadığında fırlatılır.
	- HTTP 404 Not Found dönülür çünkü istemci geçerli bir kaynak istemiş ama o kaynak bulunamamıştır.
	*/
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, exception.getMessage(), List.of()));
	}

	/*
	- DuplicateResourceException, tekil olması gereken veri tekrar oluşturulmak istendiğinde fırlatılır.
	- HTTP 409 Conflict dönülür çünkü yeni istek mevcut veri durumu ile çakışmaktadır.
	*/
	@ExceptionHandler(DuplicateResourceException.class)
	public ResponseEntity<ApiErrorResponse> handleDuplicateResourceException(DuplicateResourceException exception) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiErrorResponse.of(ErrorCode.DUPLICATE_RESOURCE, exception.getMessage(), List.of()));
	}

	/*
	- BusinessRuleViolationException, domain veya uygulama iş kuralları ihlal edildiğinde fırlatılır.
	- HTTP 400 Bad Request dönülür çünkü istek teknik olarak doğru olsa da iş kuralına aykırıdır.
	*/
	@ExceptionHandler(BusinessRuleViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleBusinessRuleViolationException(
			BusinessRuleViolationException exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiErrorResponse.of(ErrorCode.BUSINESS_RULE_VIOLATION, exception.getMessage(), List.of()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiErrorResponse> handleUnauthorizedException(UnauthorizedException exception) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiErrorResponse.of(ErrorCode.UNAUTHORIZED, exception.getMessage(), List.of()));
	}

	/*
	- Bu handler beklenmeyen tüm hataları son savunma hattı olarak yakalar.
	- İstemciye teknik detay sızdırmamak için genel bir hata mesajı döndürülür.
	- HTTP 500 Internal Server Error dönülür.
	*/
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR,
						"An unexpected error occurred", List.of()));
	}

	private ValidationErrorDetail toValidationErrorDetail(FieldError fieldError) {
		return new ValidationErrorDetail(
				fieldError.getField(),
				fieldError.getRejectedValue(),
				fieldError.getDefaultMessage());
	}
}
