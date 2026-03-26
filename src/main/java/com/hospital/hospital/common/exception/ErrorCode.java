package com.hospital.hospital.common.exception;

// Ortak hata tiplerini merkezi olarak yönetmek için kullanılır.
public enum ErrorCode {
	// Validation hataları
	VALIDATION_ERROR,
	// Kaynak bulunamadı hataları
	RESOURCE_NOT_FOUND,
	// İş kuralı ihlali hataları
	BUSINESS_RULE_VIOLATION,
	// Kaynak tekrarı hataları
	DUPLICATE_RESOURCE,
	// Yetkilendirme hataları
	UNAUTHORIZED,
	// Erişim engellendi hataları
	FORBIDDEN,
	// Sunucu hataları
	INTERNAL_SERVER_ERROR
}
