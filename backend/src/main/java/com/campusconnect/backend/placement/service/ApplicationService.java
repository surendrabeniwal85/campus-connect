package com.campusconnect.backend.placement.service;

import com.campusconnect.backend.placement.dto.EligibilityResultDto;
import com.campusconnect.backend.placement.entity.Application;
import com.campusconnect.backend.placement.entity.PlacementDrive;
import com.campusconnect.backend.placement.repository.ApplicationRepository;
import com.campusconnect.backend.placement.repository.PlacementDriveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PlacementDriveRepository driveRepository;
    private final EligibilityService eligibilityService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              PlacementDriveRepository driveRepository,
                              EligibilityService eligibilityService) {
        this.applicationRepository = applicationRepository;
        this.driveRepository = driveRepository;
        this.eligibilityService = eligibilityService;
    }

    public Application applyToDrive(Long driveId, Long studentId) {
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Placement drive not found"));

        if (LocalDateTime.now().isAfter(drive.getDeadline())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application deadline has passed");
        }

        if (applicationRepository.existsByDriveIdAndStudentId(driveId, studentId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Student has already applied to this drive");
        }

        EligibilityResultDto eligibility = eligibilityService.evaluateEligibility(driveId, studentId);
        if (!eligibility.isOverallEligible()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Student is not eligible for this drive");
        }

        Application application = new Application();
        application.setDrive(drive);
        application.setStudentId(studentId);
        application.setStatus(Application.ApplicationStatus.APPLIED);
        application.setAppliedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public void withdrawApplication(Long driveId, Long studentId) {
        Application application = applicationRepository.findByDriveIdAndStudentId(driveId, studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));

        application.setStatus(Application.ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
    }

    public List<Application> getApplicationsForDrive(Long driveId) {
        return applicationRepository.findByDriveId(driveId);
    }
}