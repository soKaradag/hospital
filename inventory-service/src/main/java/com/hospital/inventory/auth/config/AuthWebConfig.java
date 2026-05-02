package com.hospital.inventory.auth.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.hospital.inventory.auth.interceptor.AuthInterceptor;

@Configuration
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
				.excludePathPatterns(List.of("/api/inventory/system/health"));
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
