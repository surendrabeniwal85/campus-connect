package com.placementassistant.backend.placement.drives.eligibility;

import java.math.BigDecimal;

/** The facts about one student. A null value means the data is not available. */
public record StudentFacts(
                BigDecimal cgpa,
                Integer activeBacklogs,
                BigDecimal class10Percent,
                BigDecimal class12Percent,
                BigDecimal attendancePercent,
                String branch,
                Integer passingYear,
                Integer atsScore) {
}
