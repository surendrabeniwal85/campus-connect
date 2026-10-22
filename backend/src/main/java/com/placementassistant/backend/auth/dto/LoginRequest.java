package com.placementassistant.backend.auth.dto;

import com.placementassistant.backend.auth.entity.Portal;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
		@NotNull Portal portal,
		@NotBlank @Email String email,
		@NotBlank String password) {
}
