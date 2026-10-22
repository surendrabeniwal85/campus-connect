package com.campusconnect.backend.auth.dto;

import java.util.List;

public record LoginResponse(String email, List<String> roles) {

	public LoginResponse {
		roles = List.copyOf(roles);
	}
}
