package com.placementassistant.backend.placement.drives.eligibility;

public record RuleResult(String rule, String expected, String actual, RuleStatus status, String nextAction) {
}
