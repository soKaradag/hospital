package com.hospital.hospital.auth.token;

/*
 * Geçersiz token durumlarında fırlatılan istisnadır.
 * Token doğrulama, yenileme veya iptal akışlarında tokenın geçersiz olduğu
 * durumlarda kullanılır.
 */
public class InvalidTokenException extends RuntimeException {

	public InvalidTokenException(String message) {
		super(message);
	}
}
