package com.placementassistant.backend.placement.drives.criteria;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CriteriaRepository extends JpaRepository<EligibilityCriteria, Long> {

        Optional<EligibilityCriteria> findByDriveIdAndDeletedFalse(Long driveId);
}
