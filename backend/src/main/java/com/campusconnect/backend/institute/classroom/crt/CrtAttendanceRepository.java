package com.campusconnect.backend.institute.classroom.crt;

import org.springframework.data.jpa.repository.JpaRepository; import java.util.*; public interface CrtAttendanceRepository extends JpaRepository<CrtAttendance,Long>{List<CrtAttendance> findAllByStudentIdAndDeletedFalse(Long student); boolean existsByCrtSessionIdAndStudentIdAndDeletedFalse(Long session,Long student);}