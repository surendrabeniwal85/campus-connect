package com.placementassistant.backend.institute.records.service;

import com.placementassistant.backend.institute.records.entity.StudentProfile;
import com.placementassistant.backend.institute.records.repository.StudentProfileRepository;
import com.placementassistant.backend.placement.drives.eligibility.StudentFacts;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class StudentQueryService {

    private final StudentProfileRepository studentProfileRepository;

    public StudentQueryService(StudentProfileRepository studentProfileRepository) {
        this.studentProfileRepository = studentProfileRepository;
    }

    public Optional<StudentProfile> findByEmail(String email) {
        return studentProfileRepository.findByEmailIgnoreCaseAndDeletedFalse(email);
    }

    public Optional<StudentProfile> findByRollNumber(String rollNumber) {
        return studentProfileRepository.findByRollNumberAndDeletedFalse(rollNumber);
    }

    public Optional<StudentProfile> findByUserId(Long userId) {
        return studentProfileRepository.findByUserIdAndDeletedFalse(userId);
    }

    public StudentFacts getStudentFacts(String email) {
        Optional<StudentProfile> opt = findByEmail(email);
        if (opt.isEmpty()) {
            return new StudentFacts(null, null, null, null, null, null, null, null);
        }
        StudentProfile p = opt.get();
        BigDecimal attendance = BigDecimal.valueOf(92.0); // Default or query from attendance service
        return new StudentFacts(
                p.getCgpa(),
                p.getActiveBacklogs(),
                p.getClass10Percent(),
                p.getClass12Percent(),
                attendance,
                p.getBranch(),
                p.getPassingYear(),
                p.getAtsScore()
        );
    }

    public boolean isFinalYear(String email) {
        return findByEmail(email).map(StudentProfile::isFinalYear).orElse(false);
    }
}
