package com.placementassistant.backend.institute.classroom.attendance;

public class AttendanceLockedException extends RuntimeException {
    public AttendanceLockedException(String message) { super(message); }
}
