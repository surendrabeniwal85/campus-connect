
package com.campusconnect.backend.attendance.service;

import com.campusconnect.backend.institute.classroom.attendance.AttendanceRecord;
import com.campusconnect.backend.institute.classroom.attendance.AttendanceRecordRepository;
import com.campusconnect.backend.institute.classroom.attendance.AttendanceStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttendanceQueryService {

    private final AttendanceRecordRepository attendanceRecordRepository;

    public AttendanceQueryService(
        AttendanceRecordRepository attendanceRecordRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
    }

    public double percentage(Long studentId) {
        List<AttendanceRecord> records =
            attendanceRecordRepository.findAllByStudentIdAndDeletedFalse(studentId);

        if (records.isEmpty()) {
            return 0.0;
        }

        long attended = records.stream()
            .filter(record ->
                record.getStatus() == AttendanceStatus.PRESENT
                    || record.getStatus() == AttendanceStatus.LATE)
            .count();

        double percentage = attended * 100.0 / records.size();

        return Math.round(percentage * 100.0) / 100.0;
    }
}
