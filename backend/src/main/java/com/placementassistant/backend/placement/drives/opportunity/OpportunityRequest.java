package com.placementassistant.backend.placement.drives.opportunity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record OpportunityRequest(
                @NotNull OpportunityType type,
                @NotBlank @Size(max = 200) String title,
                @NotBlank @Size(max = 150) String organisation,
                String description,
                @Size(max = 500) @Pattern(regexp = "^https?://.+", message = "Link must start with http:// or https://") String link,
                @Size(max = 150) String location,
                @Size(max = 100) String stipendOrPackage,
                LocalDate startDate,
                LocalDate endDate,
                LocalDateTime deadline,
                Boolean finalYearOnly) {
}
