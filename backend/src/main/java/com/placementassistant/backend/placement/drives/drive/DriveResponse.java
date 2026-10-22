package com.placementassistant.backend.placement.drives.drive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DriveResponse(
                Long id, Long companyId, String companyName, String roleTitle,
                EmploymentType employmentType, String packageText, BigDecimal ctcLpa,
                BigDecimal stipendPerMonth, boolean hasPpo, BigDecimal ppoCtcLpa,
                Integer bondMonths, String bondDetails, String jobLocation, String otherBenefits,
                String venue, LocalDateTime reportingTime, String instructions,
                LocalDate driveDate, LocalDateTime deadline, DriveStatus status) {

        static DriveResponse from(Drive d) {
                return new DriveResponse(d.getId(), d.getCompany().getId(), d.getCompany().getName(),
                                d.getRoleTitle(), d.getEmploymentType(), d.getPackageText(), d.getCtcLpa(),
                                d.getStipendPerMonth(), d.isHasPpo(), d.getPpoCtcLpa(), d.getBondMonths(),
                                d.getBondDetails(), d.getJobLocation(), d.getOtherBenefits(), d.getVenue(),
                                d.getReportingTime(), d.getInstructions(), d.getDriveDate(), d.getDeadline(),
                                d.getStatus());
        }
}
