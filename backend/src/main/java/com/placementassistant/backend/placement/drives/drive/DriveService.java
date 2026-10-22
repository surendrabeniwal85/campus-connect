package com.placementassistant.backend.placement.drives.drive;

import com.placementassistant.backend.placement.drives.PlacementConflictException;
import com.placementassistant.backend.placement.drives.PlacementNotFoundException;
import com.placementassistant.backend.placement.drives.company.Company;
import com.placementassistant.backend.placement.drives.company.CompanyRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriveService {

        private final DriveRepository drives;
        private final CompanyRepository companies;

        public DriveService(DriveRepository drives, CompanyRepository companies) {
                this.drives = drives;
                this.companies = companies;
        }

        @Transactional(readOnly = true)
        public Page<DriveResponse> list(Pageable pageable) {
                return drives.findByDeletedFalse(pageable).map(DriveResponse::from);
        }

        @Transactional(readOnly = true)
        public Page<DriveResponse> upcoming(Pageable pageable) {
                return drives.findByStatusAndDriveDateGreaterThanEqualAndDeletedFalse(
                                DriveStatus.OPEN, LocalDate.now(), pageable).map(DriveResponse::from);
        }

        @Transactional(readOnly = true)
        public List<DriveResponse> calendar(LocalDate from, LocalDate to) {
                return drives.findByDriveDateBetweenAndDeletedFalseOrderByDriveDate(from, to)
                                .stream().map(DriveResponse::from).toList();
        }

        @Transactional(readOnly = true)
        public DriveResponse get(Long id) {
                return DriveResponse.from(find(id));
        }

        @Transactional
        public DriveResponse create(DriveRequest request) {
                check(request);
                Drive saved = drives.save(Drive.create(company(request.companyId()), request));
                return DriveResponse.from(saved);
        }

        @Transactional
        public DriveResponse update(Long id, DriveRequest request) {
                check(request);
                Drive drive = find(id);
                drive.apply(company(request.companyId()), request);
                return DriveResponse.from(drive);
        }

        private void check(DriveRequest r) {
                if (r.deadline().toLocalDate().isAfter(r.driveDate())) {
                        throw new PlacementConflictException("The deadline must not be after the drive date.");
                }
                if (Boolean.TRUE.equals(r.hasPpo()) && r.ppoCtcLpa() == null) {
                        throw new PlacementConflictException("Give the PPO package when the drive has a PPO.");
                }
        }

        private Company company(Long id) {
                return companies.findByIdAndDeletedFalse(id)
                                .orElseThrow(() -> new PlacementNotFoundException("Company not found."));
        }

        private Drive find(Long id) {
                return drives.findByIdAndDeletedFalse(id)
                                .orElseThrow(() -> new PlacementNotFoundException("Drive not found."));
        }
}
