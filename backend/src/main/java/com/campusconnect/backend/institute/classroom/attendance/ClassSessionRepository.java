package com.campusconnect.backend.institute.classroom.attendance;


import org.springframework.data.jpa.repository.JpaRepository; import java.time.LocalDate; import java.util.*;
public interface ClassSessionRepository extends JpaRepository<ClassSession,Long>{List<ClassSession> findAllBySessionDateAndDeletedFalse(LocalDate date); List<ClassSession> findAllByTeacherProfileIdAndSessionDateAndDeletedFalse(Long teacher,LocalDate date);}
