package com.campusconnect.backend.institute.classroom.teacher;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface TeacherProfileRepository extends JpaRepository<TeacherProfile,Long> {
 Optional<TeacherProfile> findByAccountIdAndDeletedFalse(Long accountId);
 List<TeacherProfile> findAllByDepartmentAndDeletedFalse(String department);
 boolean existsByAccountIdAndDeletedFalse(Long accountId);
}
