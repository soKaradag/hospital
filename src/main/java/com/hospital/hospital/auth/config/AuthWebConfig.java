package com.hospital.hospital.auth.config;

import java.util.List;

import com.hospital.hospital.auth.interceptor.AuthInterceptor;
import com.hospital.hospital.auth.token.JwtTokenService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
- Web MVC ayarları içinde auth interceptor kaydı burada yapılır.
- Amaç, access token doğrulamasını controller bazında tekrar etmeden merkezi şekilde çalıştırmaktır.
*/
@Configuration
@ConditionalOnBean(JwtTokenService.class)
public class AuthWebConfig implements WebMvcConfigurer {

	private final AuthInterceptor authInterceptor;

	public AuthWebConfig(AuthInterceptor authInterceptor) {
		this.authInterceptor = authInterceptor;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/api/**")
				.excludePathPatterns(List.of(
						"/api/auth/login",
						"/api/auth/refresh"));
	}
}
