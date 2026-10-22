package com.campusconnect.backend.placement.controller;

import com.campusconnect.backend.placement.entity.Application;
import com.campusconnect.backend.placement.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drives")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/{id}/applications")
    public ResponseEntity<Application> applyToDrive(
            @PathVariable("id") Long driveId,
            @RequestParam("studentId") Long studentId) {
        Application application = applicationService.applyToDrive(driveId, studentId);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(application);
    }

    @DeleteMapping("/{id}/applications")
    public ResponseEntity<Void> withdrawApplication(
            @PathVariable("id") Long driveId,
            @RequestParam("studentId") Long studentId) {
        applicationService.withdrawApplication(driveId, studentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/applications")
    public ResponseEntity<List<Application>> getDriveApplications(
            @PathVariable("id") Long driveId) {
        List<Application> applications = applicationService.getApplicationsForDrive(driveId);
        return ResponseEntity.ok(applications);
    }
}