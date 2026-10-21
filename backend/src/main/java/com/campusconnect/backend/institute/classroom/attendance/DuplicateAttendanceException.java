package com.campusconnect.backend.institute.classroom.attendance;

public class DuplicateAttendanceException extends RuntimeException {
    public DuplicateAttendanceException(String message) { super(message); }
}
