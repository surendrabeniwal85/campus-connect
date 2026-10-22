package com.campusconnect.backend.placement.controller;

import com.campusconnect.backend.placement.dto.EligibilityResultDto;
import com.campusconnect.backend.placement.entity.DriveCriteria;
import com.campusconnect.backend.placement.entity.DriveRound;
import com.campusconnect.backend.placement.entity.PlacementDrive;
import com.campusconnect.backend.placement.service.EligibilityService;
import com.campusconnect.backend.placement.service.PlacementDriveService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/drives")
public class PlacementDriveController {

    private final PlacementDriveService driveService;
    private final EligibilityService eligibilityService;

    public PlacementDriveController(PlacementDriveService driveService, EligibilityService eligibilityService) {
        this.driveService = driveService;
        this.eligibilityService = eligibilityService;
    }

    @GetMapping
    public ResponseEntity<List<PlacementDrive>> getAllDrives() {
        return ResponseEntity.ok(driveService.getAllDrives());
    }

    @PostMapping
    public ResponseEntity<PlacementDrive> createDrive(@Valid @RequestBody PlacementDrive drive) {
        PlacementDrive createdDrive = driveService.createDrive(drive);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDrive);
    }

    @PutMapping("/{id}/criteria")
    public ResponseEntity<DriveCriteria> saveOrUpdateCriteria(
            @PathVariable("id") Long driveId, 
            @RequestBody DriveCriteria criteria) {
        DriveCriteria saved = driveService.saveOrUpdateCriteria(driveId, criteria);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/rounds")
    public ResponseEntity<DriveRound> addOrUpdateRound(
            @PathVariable("id") Long driveId, 
            @RequestBody DriveRound round) {
        DriveRound saved = driveService.addRoundToDrive(driveId, round);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}/eligibility")
    public ResponseEntity<EligibilityResultDto> checkEligibility(
            @PathVariable("id") Long driveId, 
            @RequestParam("studentId") Long studentId) {
        EligibilityResultDto result = eligibilityService.evaluateEligibility(driveId, studentId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<PlacementDrive>> getDrivesCalendar(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        List<PlacementDrive> drives = driveService.getDrivesInCalendarRange(from, to);
        return ResponseEntity.ok(drives);
    }

    // --- NEW: Step 8 Clash Endpoint for TPO ---
    @GetMapping("/conflicts")
    public ResponseEntity<List<PlacementDrive>> getConflictingDrives() {
        return ResponseEntity.ok(driveService.getConflictingDrivesForTpo());
    }
}