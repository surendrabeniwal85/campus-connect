package com.placementassistant.backend.institute.records.repository;

import com.placementassistant.backend.institute.records.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {

    Optional<StudentProfile> findByUserIdAndDeletedFalse(Long userId);

    Optional<StudentProfile> findByRollNumberAndDeletedFalse(String rollNumber);

    Optional<StudentProfile> findByEmailIgnoreCaseAndDeletedFalse(String email);

    List<StudentProfile> findAllByDeletedFalse();

    List<StudentProfile> findAllByDepartmentAndDeletedFalse(String department);
}
