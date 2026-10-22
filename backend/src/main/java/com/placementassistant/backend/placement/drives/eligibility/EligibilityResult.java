package com.placementassistant.backend.placement.drives.eligibility;

import java.util.List;

public record EligibilityResult(Long driveId, boolean eligible, boolean hasMissingData, List<RuleResult> rules) {
}
