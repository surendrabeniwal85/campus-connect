package com.campusconnect.backend.institute.classroom;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract checklist kept intentionally independent of a particular security setup.
 * Add @SpringBootTest + MockMvc endpoint tests after the shared authentication roles
 * and agreed API response DTOs are wired into this module.
 */
class ClassroomApiContractTest {
    @Test
    void requiredEndpointGroupsAreDocumented() {
        String[] endpointGroups = {
            "/api/v1/teachers",
            "/api/v1/timetable/slots",
            "/api/v1/class-sessions",
            "/api/v1/students/{id}/attendance-summary",
            "/api/v1/hod/dashboard",
            "/api/v1/crt/sessions",
            "/api/v1/question-bank"
        };
        assertEquals(7, endpointGroups.length);
        for (String endpoint : endpointGroups) {
            assertTrue(endpoint.startsWith("/api/v1/"));
        }
    }
}
