package com.placementassistant.backend.placement.drives.criteria;

import java.math.BigDecimal;

public record CriteriaResponse(
                Long driveId, BigDecimal minCgpa, Integer maxBacklogs, BigDecimal minClass10,
                BigDecimal minClass12, BigDecimal minAttendance, String allowedBranches,
                Integer passingYear, Integer minAtsScore) {

        static CriteriaResponse from(EligibilityCriteria c) {
                return new CriteriaResponse(c.getDriveId(), c.getMinCgpa(), c.getMaxBacklogs(),
                                c.getMinClass10(), c.getMinClass12(), c.getMinAttendance(),
                                c.getAllowedBranches(), c.getPassingYear(), c.getMinAtsScore());
        }

        static CriteriaResponse empty(Long driveId) {
                return new CriteriaResponse(driveId, null, null, null, null, null, null, null, null);
        }
}
