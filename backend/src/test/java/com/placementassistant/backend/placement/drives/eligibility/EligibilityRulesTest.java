package com.placementassistant.backend.placement.drives.eligibility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.placementassistant.backend.placement.drives.eligibility.EligibilityRules.Limits;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class EligibilityRulesTest {

        private static BigDecimal d(String v) {
                return new BigDecimal(v);
        }

        private static Limits onlyCgpa(String min) {
                return new Limits(d(min), null, null, null, null, null, null, null);
        }

        private static Limits onlyAttendance(String min) {
                return new Limits(null, null, null, null, d(min), null, null, null);
        }

        private static StudentFacts facts(BigDecimal cgpa, Integer backlogs, BigDecimal attendance, String branch,
                        Integer year, Integer ats) {
                return new StudentFacts(cgpa, backlogs, null, null, attendance, branch, year, ats);
        }

        @Test
        void cgpaEqualToMinimumPasses() {
                List<RuleResult> r = EligibilityRules.evaluate(onlyCgpa("7.0"), facts(d("7.0"), null, null, null, null, null));
                assertEquals(RuleStatus.PASS, r.get(0).status());
        }

        @Test
        void cgpaBelowMinimumFailsWithReason() {
                List<RuleResult> r = EligibilityRules.evaluate(onlyCgpa("7.0"), facts(d("6.9"), null, null, null, null, null));
                assertEquals(RuleStatus.FAIL, r.get(0).status());
                assertTrue(r.get(0).nextAction().contains("6.9"));
                assertTrue(r.get(0).nextAction().contains("7"));
        }

        @Test
        void missingCgpaIsMissingNotFail() {
                List<RuleResult> r = EligibilityRules.evaluate(onlyCgpa("7.0"), facts(null, null, null, null, null, null));
                assertEquals(RuleStatus.MISSING, r.get(0).status());
        }

        @Test
        void attendanceBoundaryIsExactly75() {
                assertEquals(RuleStatus.PASS, EligibilityRules.evaluate(onlyAttendance("75"),
                                facts(null, null, d("75"), null, null, null)).get(0).status());
                assertEquals(RuleStatus.FAIL, EligibilityRules.evaluate(onlyAttendance("75"),
                                facts(null, null, d("74.9"), null, null, null)).get(0).status());
        }

        @Test
        void attendanceFailMessageMatchesTheDesign() {
                RuleResult r = EligibilityRules.evaluate(onlyAttendance("75"), facts(null, null, d("68"), null, null, null)).get(0);
                assertEquals("Your attendance is 68, you need at least 75. Attend more classes.", r.nextAction());
        }

        @Test
        void backlogsEqualToLimitPassesAndAboveFails() {
                Limits limits = new Limits(null, 1, null, null, null, null, null, null);
                assertEquals(RuleStatus.PASS, EligibilityRules.evaluate(limits, facts(null, 1, null, null, null, null)).get(0).status());
                assertEquals(RuleStatus.FAIL, EligibilityRules.evaluate(limits, facts(null, 2, null, null, null, null)).get(0).status());
                assertEquals(RuleStatus.MISSING, EligibilityRules.evaluate(limits, facts(null, null, null, null, null, null)).get(0).status());
        }

        @Test
        void branchMatchIgnoresCaseAndSpaces() {
                Limits limits = new Limits(null, null, null, null, null, "CSE, IT", null, null);
                assertEquals(RuleStatus.PASS, EligibilityRules.evaluate(limits, facts(null, null, null, "it", null, null)).get(0).status());
                assertEquals(RuleStatus.FAIL, EligibilityRules.evaluate(limits, facts(null, null, null, "MECH", null, null)).get(0).status());
                assertEquals(RuleStatus.MISSING, EligibilityRules.evaluate(limits, facts(null, null, null, null, null, null)).get(0).status());
        }

        @Test
        void passingYearMustMatch() {
                Limits limits = new Limits(null, null, null, null, null, null, 2027, null);
                assertEquals(RuleStatus.PASS, EligibilityRules.evaluate(limits, facts(null, null, null, null, 2027, null)).get(0).status());
                assertEquals(RuleStatus.FAIL, EligibilityRules.evaluate(limits, facts(null, null, null, null, 2026, null)).get(0).status());
        }

        @Test
        void atsScoreMissingWhenNoResume() {
                Limits limits = new Limits(null, null, null, null, null, null, null, 50);
                assertEquals(RuleStatus.MISSING, EligibilityRules.evaluate(limits, facts(null, null, null, null, null, null)).get(0).status());
                assertEquals(RuleStatus.PASS, EligibilityRules.evaluate(limits, facts(null, null, null, null, null, 50)).get(0).status());
        }

        @Test
        void unsetLimitsProduceNoRules() {
                Limits none = new Limits(null, null, null, null, null, null, null, null);
                assertTrue(EligibilityRules.evaluate(none, facts(null, null, null, null, null, null)).isEmpty());
        }

        @Test
        void failMakesIneligibleButMissingAloneDoesNot() {
                Limits limits = new Limits(d("7.0"), null, null, null, d("75"), null, null, null);
                EligibilityResult failed = EligibilityRules.summarize(1L,
                                EligibilityRules.evaluate(limits, facts(d("6.0"), null, d("80"), null, null, null)));
                assertFalse(failed.eligible());

                EligibilityResult missingOnly = EligibilityRules.summarize(1L,
                                EligibilityRules.evaluate(limits, facts(d("8.0"), null, null, null, null, null)));
                assertTrue(missingOnly.eligible());
                assertTrue(missingOnly.hasMissingData());

                EligibilityResult allPass = EligibilityRules.summarize(1L,
                                EligibilityRules.evaluate(limits, facts(d("8.0"), null, d("80"), null, null, null)));
                assertTrue(allPass.eligible());
                assertFalse(allPass.hasMissingData());
        }
}
