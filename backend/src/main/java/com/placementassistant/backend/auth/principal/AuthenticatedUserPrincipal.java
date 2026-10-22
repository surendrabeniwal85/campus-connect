package com.placementassistant.backend.auth.principal;

import java.security.Principal;
import java.util.List;

public record AuthenticatedUserPrincipal(String name, List<String> roleNames) implements Principal {

	public AuthenticatedUserPrincipal {
		roleNames = List.copyOf(roleNames);
	}

	@Override
	public String getName() {
		return name;
	}
}
