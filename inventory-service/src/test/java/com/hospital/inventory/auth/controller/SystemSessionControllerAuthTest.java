package com.hospital.inventory.auth.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hospital.inventory.auth.config.AuthWebConfig;
import com.hospital.inventory.auth.context.CurrentUserContext;
import com.hospital.inventory.auth.dto.AuthenticatedUserResponse;
import com.hospital.inventory.auth.interceptor.AuthInterceptor;
import com.hospital.inventory.auth.model.PermissionCodes;
import com.hospital.inventory.auth.service.AuthorizationServiceImpl;
import com.hospital.inventory.auth.service.CoreAuthIntrospectionClient;
import com.hospital.inventory.auth.token.JwtTokenService;
import com.hospital.inventory.auth.token.TokenPrincipal;
import com.hospital.inventory.auth.token.TokenType;
import com.hospital.inventory.common.exception.GlobalExceptionHandler;

@WebMvcTest(SystemSessionController.class)
@Import({
		GlobalExceptionHandler.class,
		AuthWebConfig.class,
		AuthInterceptor.class,
		AuthorizationServiceImpl.class,
		CurrentUserContext.class
})
class SystemSessionControllerAuthTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private JwtTokenService jwtTokenService;

	@MockitoBean
	private CoreAuthIntrospectionClient coreAuthIntrospectionClient;

	@Test
	void sessionShouldReturnUnauthorizedWhenAuthorizationHeaderIsMissing() throws Exception {
		mockMvc.perform(get("/api/inventory/system/session"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.code", is("UNAUTHORIZED")));
	}

	@Test
	void sessionShouldReturnForbiddenWhenInventoryPermissionIsMissing() throws Exception {
		TokenPrincipal principal = new TokenPrincipal(
				UUID.randomUUID(),
				"admin",
				"ADMIN",
				TokenType.ACCESS,
				Instant.now(),
				Instant.now().plusSeconds(900));
		when(jwtTokenService.parseAccessToken("valid-token")).thenReturn(principal);
		when(coreAuthIntrospectionClient.introspect("Bearer valid-token"))
				.thenReturn(authenticatedUser(principal.userId(), List.of("patients.read")));

		mockMvc.perform(get("/api/inventory/system/session")
						.header("Authorization", "Bearer valid-token"))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.success", is(false)))
				.andExpect(jsonPath("$.code", is("FORBIDDEN")));
	}

	@Test
	void sessionShouldReturnCurrentUserWhenInventoryPermissionExists() throws Exception {
		TokenPrincipal principal = new TokenPrincipal(
				UUID.randomUUID(),
				"admin",
				"ADMIN",
				TokenType.ACCESS,
				Instant.now(),
				Instant.now().plusSeconds(900));
		when(jwtTokenService.parseAccessToken("valid-token")).thenReturn(principal);
		when(coreAuthIntrospectionClient.introspect("Bearer valid-token"))
				.thenReturn(authenticatedUser(principal.userId(), List.of(PermissionCodes.INVENTORY_ITEMS_READ)));

		mockMvc.perform(get("/api/inventory/system/session")
						.header("Authorization", "Bearer valid-token"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.success", is(true)))
				.andExpect(jsonPath("$.data.username", is("admin")));
	}

	private AuthenticatedUserResponse authenticatedUser(UUID userId, List<String> permissions) {
		AuthenticatedUserResponse user = new AuthenticatedUserResponse();
		user.setId(userId);
		user.setUsername("admin");
		user.setRoles(List.of("ADMIN"));
		user.setPermissions(permissions);
		return user;
	}
}
