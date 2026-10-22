package com.campusconnect.backend.placement.selection.candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<DriveFeedback, Long> {
    List<DriveFeedback> findByDriveId(Long driveId);
}