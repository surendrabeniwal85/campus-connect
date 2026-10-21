package com.placementassistant.backend.institute.classroom.attendance;

public class AttendanceForbiddenException extends RuntimeException {
    public AttendanceForbiddenException(String message) { super(message); }
}
