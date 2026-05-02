package com.hospital.inventory.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.hospital.inventory.auth.dto.AuthenticatedUserResponse;
import com.hospital.inventory.common.dto.ApiResponse;
import com.hospital.inventory.common.exception.UnauthorizedException;

@Service
public class CoreAuthIntrospectionClient {

	private final RestClient restClient;

	public CoreAuthIntrospectionClient(
			RestClient.Builder restClientBuilder,
			@Value("${inventory.core.base-url}") String baseUrl) {
		this.restClient = restClientBuilder.baseUrl(baseUrl).build();
	}

	public AuthenticatedUserResponse introspect(String authorizationHeader) {
		try {
			ApiResponse<AuthenticatedUserResponse> response = restClient.get()
					.uri("/api/internal/auth/introspect")
					.header("Authorization", authorizationHeader)
					.retrieve()
					.body(new ParameterizedTypeReference<ApiResponse<AuthenticatedUserResponse>>() {
					});

			if (response == null || response.getData() == null) {
				throw new UnauthorizedException("Core auth introspection returned an empty response");
			}
			return response.getData();
		} catch (RestClientException exception) {
			throw new UnauthorizedException("Core auth introspection failed");
		}
	}
}
