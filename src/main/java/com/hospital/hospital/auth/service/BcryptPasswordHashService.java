package com.hospital.hospital.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/*
- Bu servis kullanıcı parolalarını BCrypt ile hash'lemek için kullanılır.
- BCrypt, salt üretimini ve güvenli karşılaştırmayı kendi içinde yönettiği için manuel hash koduna göre daha doğru bir tercihtir.
- Burada sadece crypto yardımcı sınıfı kullanılır; Spring Security auth altyapısı kullanılmaz.
*/
@Service
public class BcryptPasswordHashService implements PasswordHashService {

	// Tek bir encoder örneği yeterlidir; cost değeri varsayılan güvenli seviyede bırakılmıştır.
	private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Override
	// Düz parolayı BCrypt hash formatına çevirir.
	public String hash(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	@Override
	// Login sırasında gelen parola ile saklanan BCrypt hash değerini güvenli şekilde karşılaştırır.
	public boolean matches(String rawPassword, String storedHash) {
		return passwordEncoder.matches(rawPassword, storedHash);
	}
}
