package com.placementassistant.backend.placement.drives.company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyRequest(
                @NotBlank @Size(max = 150) String name,
                @Size(max = 100) String industry,
                @Size(max = 255) String website) {
}
