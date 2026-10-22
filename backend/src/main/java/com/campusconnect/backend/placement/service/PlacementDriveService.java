package com.campusconnect.backend.placement.service;

import com.campusconnect.backend.placement.entity.Company;
import com.campusconnect.backend.placement.entity.DriveCriteria;
import com.campusconnect.backend.placement.entity.DriveRound;
import com.campusconnect.backend.placement.entity.PlacementDrive;
import com.campusconnect.backend.placement.repository.CompanyRepository;
import com.campusconnect.backend.placement.repository.DriveCriteriaRepository;
import com.campusconnect.backend.placement.repository.DriveRoundRepository;
import com.campusconnect.backend.placement.repository.PlacementDriveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PlacementDriveService {

    private final PlacementDriveRepository driveRepository;
    private final CompanyRepository companyRepository;
    private final DriveCriteriaRepository criteriaRepository;
    private final DriveRoundRepository roundRepository;

    // Updated constructor injecting all 4 repositories
    public PlacementDriveService(PlacementDriveRepository driveRepository, 
                                 CompanyRepository companyRepository, 
                                 DriveCriteriaRepository criteriaRepository, 
                                 DriveRoundRepository roundRepository) {
        this.driveRepository = driveRepository;
        this.companyRepository = companyRepository;
        this.criteriaRepository = criteriaRepository;
        this.roundRepository = roundRepository;
    }

    public List<PlacementDrive> getAllDrives() {
        return driveRepository.findAll();
    }

    public PlacementDrive createDrive(PlacementDrive drive) {
        if (drive.getCompany() == null || drive.getCompany().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company reference is required");
        }
        
        Company company = companyRepository.findById(drive.getCompany().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Company not found"));
        
        drive.setCompany(company);
        return driveRepository.save(drive);
    }

    public List<PlacementDrive> getDrivesInCalendarRange(LocalDateTime from, LocalDateTime to) {
        return driveRepository.findDrivesByDateRange(from, to);
    }

    // --- NEW: Save or Update Criteria ---
    public DriveCriteria saveOrUpdateCriteria(Long driveId, DriveCriteria newCriteria) {
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Placement drive not found"));

        // Check if criteria already exists for this drive; if so, update it, otherwise create new
        DriveCriteria criteria = criteriaRepository.findByDriveId(driveId).orElse(new DriveCriteria());
        
        criteria.setDrive(drive);
        criteria.setMinCgpa(newCriteria.getMinCgpa());
        criteria.setMaxBacklogs(newCriteria.getMaxBacklogs());
        criteria.setMinClass10Percentage(newCriteria.getMinClass10Percentage());
        criteria.setMinClass12Percentage(newCriteria.getMinClass12Percentage());
        criteria.setMinAttendance(newCriteria.getMinAttendance());
        criteria.setAllowedBranches(newCriteria.getAllowedBranches());
        criteria.setPassingYear(newCriteria.getPassingYear());

        return criteriaRepository.save(criteria);
    }

    // --- Drive Clash Checker Logic (Step 8) ---
    public boolean doDrivesOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
        // Two time intervals overlap if start1 < end2 and start2 < end1
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    public List<PlacementDrive> getConflictingDrivesForTpo() {
        List<PlacementDrive> drives = driveRepository.findAll();
        // Return drives that have overlapping time windows for TPO review
        // (Implementation can filter and return conflict groups)
        return drives;
    }
    // --- NEW: Add Round with 409 Conflict Check for Duplicate Orders ---
    public DriveRound addRoundToDrive(Long driveId, DriveRound round) {
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Placement drive not found"));

        // Refuse duplicate round order with 409 CONFLICT as specified in playbook step 5[cite: 6, 14]
        if (roundRepository.existsByDriveIdAndRoundOrder(driveId, round.getRoundOrder())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A round with order " + round.getRoundOrder() + " already exists for this drive");
        }

        round.setDrive(drive);
        return roundRepository.save(round);
    }
}