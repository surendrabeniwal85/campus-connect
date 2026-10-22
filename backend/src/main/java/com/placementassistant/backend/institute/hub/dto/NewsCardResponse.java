package com.placementassistant.backend.institute.hub.dto;

import java.time.Instant;

public record NewsCardResponse(
        String id,
        String type, // DRIVE, TALK, HACKATHON, INTERNSHIP, OFF_CAMPUS, ROUND_UPDATE
        String headline,
        String summary,
        String date,
        String deadline,
        String targetLink,
        String badgeColor,
        String nextActionLabel,
        boolean finalYearOnly
) {
}
