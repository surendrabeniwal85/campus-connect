package com.placementassistant.backend.placement.drives.drive;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DriveRequest(
                @NotNull Long companyId,
                @NotBlank @Size(max = 150) String roleTitle,
                EmploymentType employmentType,
                @Size(max = 100) String packageText,
                @DecimalMin("0.0") BigDecimal ctcLpa,
                @DecimalMin("0.0") BigDecimal stipendPerMonth,
                Boolean hasPpo,
                @DecimalMin("0.0") BigDecimal ppoCtcLpa,
                @Min(0) Integer bondMonths,
                String bondDetails,
                @Size(max = 150) String jobLocation,
                String otherBenefits,
                @Size(max = 200) String venue,
                LocalDateTime reportingTime,
                String instructions,
                @NotNull LocalDate driveDate,
                @NotNull LocalDateTime deadline,
                DriveStatus status) {
}
