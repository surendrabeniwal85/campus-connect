package com.placementassistant.backend.auth.dto;

import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String displayName,
        List<String> roles,
        String portal,
        boolean enabled,
        String createdAt
) {
}
