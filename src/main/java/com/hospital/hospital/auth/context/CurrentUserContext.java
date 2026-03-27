package com.hospital.hospital.auth.context;

import com.hospital.hospital.auth.token.TokenPrincipal;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/*
- Bu sınıf, request süresince oturum sahibinin kimlik bilgisini taşımak için kullanılır.
- Request scope seçilmesinin nedeni, her HTTP isteğinin kendi kullanıcı context'ine sahip olmasıdır.
- Böylece farklı istekler arasında kullanıcı bilgisi karışmaz.
*/
@Component
@RequestScope
public class CurrentUserContext {

	private TokenPrincipal principal;

	// Interceptor token'ı doğruladığında çözülen kullanıcı bilgisi buraya yazılır.
	public void setPrincipal(TokenPrincipal principal) {
		this.principal = principal;
	}

	// Service veya authorization katmanı mevcut kullanıcıyı buradan okuyabilir.
	public TokenPrincipal getPrincipal() {
		return principal;
	}

	// Bazı endpoint'lerde anonim erişim olabilir; bu yüzden context'in dolu olup olmadığı da kontrol edilebilir.
	public boolean isAuthenticated() {
		return principal != null;
	}
}
