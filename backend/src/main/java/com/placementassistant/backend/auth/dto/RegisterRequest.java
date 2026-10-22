package com.placementassistant.backend.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Full name is required")
        String fullName,

        @NotBlank(message = "Roll number is required")
        String rollNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        String department,
        String branch,
        Integer semester,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}
