package com.placementassistant.backend.institute.classroom.attendance;


import org.springframework.data.jpa.repository.JpaRepository; import java.util.*; public interface AttendanceCorrectionRepository extends JpaRepository<AttendanceCorrection,Long>{List<AttendanceCorrection> findAllByState(String state);}
