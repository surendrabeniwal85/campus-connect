package com.campusconnect.backend.placement.selection.candidate;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LiveUpdateRepository extends JpaRepository<LiveSessionUpdate, Long> {
    List<LiveSessionUpdate> findByDriveId(Long driveId);
}