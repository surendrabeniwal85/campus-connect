package com.placementassistant.backend.placement.drives.eligibility;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public final class OfferPathDtos {

    private OfferPathDtos() {}

    public record SimulationRequest(
            List<Long> driveIds,
            BigDecimal simulatedAttendance,
            BigDecimal simulatedCgpa,
            Integer simulatedAtsScore,
            Integer simulatedBacklogs
    ) {}

    public record RuleTransition(
            String rule,
            String fromStatus,
            String toStatus,
            String reason
    ) {}

    public record NewlyUnlockedDrive(
            Long driveId,
            String companyName,
            String roleTitle,
            List<RuleTransition> changedRules
    ) {}

    public record SimulationResponse(
            List<EligibilityResult> actualResults,
            List<EligibilityResult> simulatedResults,
            List<NewlyUnlockedDrive> newlyUnlockedDrives
    ) {}

    public record OneRuleAwayStudentDto(
            Long studentId,
            String fullName,
            String rollNumber,
            String email,
            String unmetRule,
            String actualValue,
            String requiredValue,
            String nextAction
    ) {}

    public record OneRuleAwayResponse(
            Long driveId,
            String companyName,
            String roleTitle,
            int totalAssessed,
            int eligibleCount,
            int oneRuleAwayCount,
            int missingDataCount,
            Map<String, Integer> unmetRuleBreakdown,
            List<OneRuleAwayStudentDto> candidates
    ) {}

    public record NudgeRequest(
            String customMessage
    ) {}

    public record NudgeResponse(
            Long driveId,
            String driveTitle,
            int targetedCount,
            int sentCount,
            String status,
            String message
    ) {}
}
