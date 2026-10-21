package com.campusconnect.backend.institute.classroom;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceSummaryRulesTest {
    private double percentage(long attended, long total) {
        if (total == 0) return 0.0;
        return attended * 100.0 / total;
    }

    @Test
    void zeroSessionsReturnsZeroPercent() {
        assertEquals(0.0, percentage(0, 0));
    }

    @Test
    void exactlySeventyFivePercentIsNotShortage() {
        double value = percentage(3, 4);
        assertEquals(75.0, value);
        assertFalse(value < 75.0);
    }

    @Test
    void belowSeventyFivePercentIsShortage() {
        assertTrue(percentage(74, 100) < 75.0);
    }
}
