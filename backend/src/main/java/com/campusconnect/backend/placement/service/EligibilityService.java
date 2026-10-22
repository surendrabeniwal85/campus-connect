package com.campusconnect.backend.placement.service;

import com.campusconnect.backend.placement.dto.EligibilityResultDto;
import com.campusconnect.backend.placement.entity.DriveCriteria;
import com.campusconnect.backend.placement.repository.DriveCriteriaRepository;
import com.campusconnect.backend.placement.repository.PlacementDriveRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class EligibilityService {

    private final DriveCriteriaRepository criteriaRepository;
    private final PlacementDriveRepository driveRepository;

    public EligibilityService(DriveCriteriaRepository criteriaRepository, PlacementDriveRepository driveRepository) {
        this.criteriaRepository = criteriaRepository;
        this.driveRepository = driveRepository;
    }

    public EligibilityResultDto evaluateEligibility(Long driveId, Long studentId) {
        // Ensure drive exists
        if (!driveRepository.existsById(driveId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Placement drive not found");
        }

        DriveCriteria criteria = criteriaRepository.findByDriveId(driveId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Criteria not configured for this drive"));

        List<EligibilityResultDto.RuleEvaluation> evaluations = new ArrayList<>();
        boolean overallPass = true;

        // 1. CGPA Rule Check (integrate with Aliza's StudentQueryService later)
        Double studentCgpa = 7.5; // Stubbed actual value for now
        if (studentCgpa == null) {
            evaluations.add(new EligibilityResultDto.RuleEvaluation("CGPA", criteria.getMinCgpa(), null, "MISSING", "Student CGPA record is missing"));
            overallPass = false;
        } else if (criteria.getMinCgpa() != null && studentCgpa >= criteria.getMinCgpa()) {
            evaluations.add(new EligibilityResultDto.RuleEvaluation("CGPA", criteria.getMinCgpa(), studentCgpa, "PASS", "CGPA requirement met"));
        } else {
            evaluations.add(new EligibilityResultDto.RuleEvaluation("CGPA", criteria.getMinCgpa(), studentCgpa, "FAIL", "CGPA " + studentCgpa + ", need " + criteria.getMinCgpa()));
            overallPass = false;
        }

        // 2. Attendance Rule Check (integrate with Naman's AttendanceQueryService later)
        Double studentAttendance = 68.0; // Stubbed actual value for now
        if (studentAttendance == null) {
            evaluations.add(new EligibilityResultDto.RuleEvaluation("Attendance", criteria.getMinAttendance(), null, "MISSING", "Attendance record is missing"));
            overallPass = false;
        } else if (criteria.getMinAttendance() != null && studentAttendance >= criteria.getMinAttendance()) {
            evaluations.add(new EligibilityResultDto.RuleEvaluation("Attendance", criteria.getMinAttendance(), studentAttendance, "PASS", "Attendance requirement met"));
        } else {
            evaluations.add(new EligibilityResultDto.RuleEvaluation("Attendance", criteria.getMinAttendance(), studentAttendance, "FAIL", "Attendance " + studentAttendance + "%, need " + criteria.getMinAttendance() + "%"));
            overallPass = false;
        }

        return new EligibilityResultDto(studentId, driveId, overallPass, evaluations);
    }
}