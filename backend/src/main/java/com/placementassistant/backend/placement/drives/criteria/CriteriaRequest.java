package com.placementassistant.backend.placement.drives.criteria;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CriteriaRequest(
                @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal minCgpa,
                @Min(0) Integer maxBacklogs,
                @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal minClass10,
                @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal minClass12,
                @DecimalMin("0.0") @DecimalMax("100.0") BigDecimal minAttendance,
                @Size(max = 255) String allowedBranches,
                @Min(2000) @Max(2100) Integer passingYear,
                @Min(0) @Max(100) Integer minAtsScore) {
}
