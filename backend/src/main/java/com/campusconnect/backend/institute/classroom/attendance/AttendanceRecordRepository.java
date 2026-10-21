package com.campusconnect.backend.institute.classroom.attendance;


import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord,Long>{List<AttendanceRecord> findAllByClassSessionIdAndDeletedFalse(Long session); List<AttendanceRecord> findAllByStudentIdAndDeletedFalse(Long student); boolean existsByClassSessionIdAndStudentIdAndDeletedFalse(Long session,Long student);}
