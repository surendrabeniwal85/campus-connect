package com.campusconnect.backend.attendance.service;

import com.campusconnect.backend.institute.classroom.attendance.AttendanceRecordRepository;
import org.springframework.stereotype.Service;

@Service
public class AttendanceQueryService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceQueryService(
        AttendanceRecordRepository attendanceRecordRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    public double percentage(Long studentId) {
        // TODO: Replace with repository queries that match your entity.
        return 0.0;
    }
}
