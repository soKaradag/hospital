package com.hospital.hospital.auth.service;

public interface PasswordHashService {

	// Düz parolayı kalıcı olarak saklanabilecek hash formuna çevirir.
	String hash(String rawPassword);

	// İstemciden gelen düz parola ile veritabanındaki hash değerini karşılaştırır.
	boolean matches(String rawPassword, String storedHash);
}
