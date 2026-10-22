package com.placementassistant.backend.placement.drives.criteria;

import com.placementassistant.backend.placement.drives.PlacementNotFoundException;
import com.placementassistant.backend.placement.drives.drive.DriveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CriteriaService {

        private final CriteriaRepository criteria;
        private final DriveRepository drives;

        public CriteriaService(CriteriaRepository criteria, DriveRepository drives) {
                this.criteria = criteria;
                this.drives = drives;
        }

        @Transactional(readOnly = true)
        public CriteriaResponse get(Long driveId) {
                requireDrive(driveId);
                return criteria.findByDriveIdAndDeletedFalse(driveId)
                                .map(CriteriaResponse::from)
                                .orElseGet(() -> CriteriaResponse.empty(driveId));
        }

        @Transactional
        public CriteriaResponse save(Long driveId, CriteriaRequest request) {
                requireDrive(driveId);
                EligibilityCriteria row = criteria.findByDriveIdAndDeletedFalse(driveId)
                                .orElseGet(() -> new EligibilityCriteria(driveId));
                row.apply(request);
                return CriteriaResponse.from(criteria.save(row));
        }

        private void requireDrive(Long driveId) {
                drives.findByIdAndDeletedFalse(driveId)
                                .orElseThrow(() -> new PlacementNotFoundException("Drive not found."));
        }
}
