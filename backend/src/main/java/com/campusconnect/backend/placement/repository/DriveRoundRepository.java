package com.campusconnect.backend.placement.repository;

import com.campusconnect.backend.placement.entity.DriveRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriveRoundRepository extends JpaRepository<DriveRound, Long> {
    List<DriveRound> findByDriveIdOrderByRoundOrderAsc(Long driveId);
    boolean existsByDriveIdAndRoundOrder(Long driveId, Integer roundOrder);
}