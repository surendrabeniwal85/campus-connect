package com.campusconnect.backend.institute.classroom;

import com.campusconnect.backend.institute.classroom.attendance.AttendanceStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AttendanceStatusTest {
    @Test
    void attendanceStatusesIncludePresentAbsentAndLate() {
        assertEquals(3, AttendanceStatus.values().length);
        assertNotNull(AttendanceStatus.valueOf("PRESENT"));
        assertNotNull(AttendanceStatus.valueOf("ABSENT"));
        assertNotNull(AttendanceStatus.valueOf("LATE"));
    }
}
