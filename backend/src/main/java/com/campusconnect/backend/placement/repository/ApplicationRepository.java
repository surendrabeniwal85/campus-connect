package com.campusconnect.backend.placement.repository;

import com.campusconnect.backend.placement.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByDriveIdAndStudentId(Long driveId, Long studentId);
    Optional<Application> findByDriveIdAndStudentId(Long driveId, Long studentId);
    List<Application> findByDriveId(Long driveId);
}