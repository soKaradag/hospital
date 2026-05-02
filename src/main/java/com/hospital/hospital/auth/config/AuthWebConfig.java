package com.hospital.hospital.auth.config;

import java.util.List;

import com.hospital.hospital.auth.interceptor.AuthInterceptor;
import com.hospital.hospital.auth.token.JwtTokenService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
- Web MVC ayarları içinde auth interceptor kaydı burada yapılır.
- Amaç, access token doğrulamasını controller bazında tekrar etmeden merkezi şekilde çalıştırmaktır.
*/
@Configuration
@ConditionalOnBean(JwtTokenService.class)
public class AuthWebConfig implements WebMvcConfigurer {

	private final AuthInterceptor authInterceptor;
	private final String allowedOrigin;

	public AuthWebConfig(
			AuthInterceptor authInterceptor,
			@Value("${app.cors.allowed-origin:http://localhost:5173}") String allowedOrigin) {
		this.authInterceptor = authInterceptor;
		this.allowedOrigin = allowedOrigin;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/api/**")
				.excludePathPatterns(List.of(
						"/api/auth/login",
						"/api/auth/refresh"));
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/api/**")
				.allowedOrigins(allowedOrigin)
				.allowedMethods(
						HttpMethod.GET.name(),
						HttpMethod.POST.name(),
						HttpMethod.PUT.name(),
						HttpMethod.DELETE.name(),
						HttpMethod.OPTIONS.name())
				.allowedHeaders("*")
				.allowCredentials(false)
				.maxAge(3600);
	}
}
