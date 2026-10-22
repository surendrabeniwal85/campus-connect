package com.campusconnect.backend.placement.repository;

import com.campusconnect.backend.placement.entity.PlacementDrive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {

    // Query to find drives scheduled between a specific 'from' and 'to' date range for the calendar API
    @Query("SELECT d FROM PlacementDrive d WHERE d.driveDate BETWEEN :startDate AND :endDate")
    List<PlacementDrive> findDrivesByDateRange(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate
    );
}