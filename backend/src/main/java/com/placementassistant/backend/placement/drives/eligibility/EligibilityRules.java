package com.placementassistant.backend.placement.drives.eligibility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Fixed, visible rules. Equal to the limit passes. A missing value is MISSING, never FAIL. */
public final class EligibilityRules {

        private static final String NOT_AVAILABLE = "not available";

        private EligibilityRules() {
        }

        public record Limits(BigDecimal minCgpa, Integer maxBacklogs, BigDecimal minClass10, BigDecimal minClass12,
                        BigDecimal minAttendance, String allowedBranches, Integer passingYear, Integer minAtsScore) {
        }

        public static EligibilityResult summarize(Long driveId, List<RuleResult> rules) {
                boolean anyFail = rules.stream().anyMatch(r -> r.status() == RuleStatus.FAIL);
                boolean anyMissing = rules.stream().anyMatch(r -> r.status() == RuleStatus.MISSING);
                return new EligibilityResult(driveId, !anyFail, anyMissing, rules);
        }

        public static List<RuleResult> evaluate(Limits l, StudentFacts s) {
                List<RuleResult> out = new ArrayList<>();
                if (l.minCgpa() != null) {
                        out.add(atLeast("Minimum CGPA", "CGPA", l.minCgpa(), s.cgpa(), "Work on improving your CGPA."));
                }
                if (l.maxBacklogs() != null) {
                        out.add(atMost(l.maxBacklogs(), s.activeBacklogs()));
                }
                if (l.minClass10() != null) {
                        out.add(atLeast("Minimum Class 10 percent", "Class 10 percent", l.minClass10(), s.class10Percent(), ""));
                }
                if (l.minClass12() != null) {
                        out.add(atLeast("Minimum Class 12 percent", "Class 12 percent", l.minClass12(), s.class12Percent(), ""));
                }
                if (l.minAttendance() != null) {
                        out.add(atLeast("Minimum attendance percent", "attendance", l.minAttendance(),
                                        s.attendancePercent(), "Attend more classes."));
                }
                if (l.allowedBranches() != null && !l.allowedBranches().isBlank()) {
                        out.add(branch(l.allowedBranches(), s.branch()));
                }
                if (l.passingYear() != null) {
                        out.add(year(l.passingYear(), s.passingYear()));
                }
                if (l.minAtsScore() != null) {
                        BigDecimal ats = s.atsScore() == null ? null : BigDecimal.valueOf(s.atsScore());
                        out.add(atLeast("Minimum ATS score", "ATS score", BigDecimal.valueOf(l.minAtsScore()), ats,
                                        "Upload a resume and improve its ATS score."));
                }
                return out;
        }

        private static String plain(BigDecimal value) {
                return value.stripTrailingZeros().toPlainString();
        }

        private static RuleResult atLeast(String rule, String label, BigDecimal min, BigDecimal actual, String hint) {
                String expected = ">= " + plain(min);
                if (actual == null) {
                        return new RuleResult(rule, expected, NOT_AVAILABLE, RuleStatus.MISSING,
                                        "Your " + label + " is not available yet.");
                }
                if (actual.compareTo(min) >= 0) {
                        return new RuleResult(rule, expected, plain(actual), RuleStatus.PASS, "");
                }
                String gap = "Your " + label + " is " + plain(actual) + ", you need at least " + plain(min) + ".";
                return new RuleResult(rule, expected, plain(actual), RuleStatus.FAIL, (gap + " " + hint).trim());
        }

        private static RuleResult atMost(Integer max, Integer actual) {
                String rule = "Maximum active backlogs";
                String expected = "<= " + max;
                if (actual == null) {
                        return new RuleResult(rule, expected, NOT_AVAILABLE, RuleStatus.MISSING,
                                        "Your backlog count is not available yet.");
                }
                if (actual <= max) {
                        return new RuleResult(rule, expected, String.valueOf(actual), RuleStatus.PASS, "");
                }
                return new RuleResult(rule, expected, String.valueOf(actual), RuleStatus.FAIL,
                                "You have " + actual + " active backlogs, the limit is " + max + ". Clear your backlogs.");
        }

        private static RuleResult branch(String allowed, String actual) {
                String rule = "Allowed branches";
                if (actual == null || actual.isBlank()) {
                        return new RuleResult(rule, allowed, NOT_AVAILABLE, RuleStatus.MISSING,
                                        "Your branch is not available yet.");
                }
                boolean ok = Arrays.stream(allowed.split(",")).map(String::trim)
                                .anyMatch(b -> b.equalsIgnoreCase(actual.trim()));
                if (ok) {
                        return new RuleResult(rule, allowed, actual, RuleStatus.PASS, "");
                }
                return new RuleResult(rule, allowed, actual, RuleStatus.FAIL,
                                "This drive is open only to: " + allowed + ".");
        }

        private static RuleResult year(Integer required, Integer actual) {
                String rule = "Passing year";
                if (actual == null) {
                        return new RuleResult(rule, String.valueOf(required), NOT_AVAILABLE, RuleStatus.MISSING,
                                        "Your passing year is not available yet.");
                }
                if (actual.equals(required)) {
                        return new RuleResult(rule, String.valueOf(required), String.valueOf(actual), RuleStatus.PASS, "");
                }
                return new RuleResult(rule, String.valueOf(required), String.valueOf(actual), RuleStatus.FAIL,
                                "This drive is for the " + required + " batch.");
        }
}
