package com.hospital.inventory.auth.context;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import com.hospital.inventory.auth.dto.AuthenticatedUserResponse;
import com.hospital.inventory.auth.token.TokenPrincipal;

@Component
@RequestScope
public class CurrentUserContext {

	private TokenPrincipal principal;
	private List<String> roles = List.of();
	private Set<String> permissions = Set.of();
	private AuthenticatedUserResponse user;

	public void setPrincipal(TokenPrincipal principal) {
		this.principal = principal;
	}

	public TokenPrincipal getPrincipal() {
		return principal;
	}

	public boolean isAuthenticated() {
		return principal != null;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public Set<String> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	public AuthenticatedUserResponse getUser() {
		return user;
	}

	public void setUser(AuthenticatedUserResponse user) {
		this.user = user;
	}
}
