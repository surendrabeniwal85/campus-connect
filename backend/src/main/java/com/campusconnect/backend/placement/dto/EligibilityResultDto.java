package com.campusconnect.backend.placement.dto;

import java.util.List;

public class EligibilityResultDto {
    private Long studentId;
    private Long driveId;
    private boolean overallEligible;
    private List<RuleEvaluation> evaluations;

    public EligibilityResultDto() {}

    public EligibilityResultDto(Long studentId, Long driveId, boolean overallEligible, List<RuleEvaluation> evaluations) {
        this.studentId = studentId;
        this.driveId = driveId;
        this.overallEligible = overallEligible;
        this.evaluations = evaluations;
    }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public Long getDriveId() { return driveId; }
    public void setDriveId(Long driveId) { this.driveId = driveId; }

    public boolean isOverallEligible() { return overallEligible; }
    public void setOverallEligible(boolean overallEligible) { this.overallEligible = overallEligible; }

    public List<RuleEvaluation> getEvaluations() { return evaluations; }
    public void setEvaluations(List<RuleEvaluation> evaluations) { this.evaluations = evaluations; }

    public static class RuleEvaluation {
        private String ruleName;
        private Object expected;
        private Object actual;
        private String status;
        private String reason;

        public RuleEvaluation() {}

        public RuleEvaluation(String ruleName, Object expected, Object actual, String status, String reason) {
            this.ruleName = ruleName;
            this.expected = expected;
            this.actual = actual;
            this.status = status;
            this.reason = reason;
        }

        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }

        public Object getExpected() { return expected; }
        public void setExpected(Object expected) { this.expected = expected; }

        public Object getActual() { return actual; }
        public void setActual(Object actual) { this.actual = actual; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}