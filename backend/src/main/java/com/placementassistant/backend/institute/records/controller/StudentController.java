package com.placementassistant.backend.institute.records.controller;

import com.placementassistant.backend.institute.records.entity.StudentProfile;
import com.placementassistant.backend.institute.records.repository.StudentProfileRepository;
import com.placementassistant.backend.institute.records.service.StudentQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentQueryService studentQueryService;

    public StudentController(
            StudentProfileRepository studentProfileRepository,
            StudentQueryService studentQueryService) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentQueryService = studentQueryService;
    }

    @GetMapping("/me")
    public ResponseEntity<StudentProfile> getMyProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        String email = authentication.getName();
        return studentQueryService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<StudentProfile>> listStudents() {
        return ResponseEntity.ok(studentProfileRepository.findAllByDeletedFalse());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentProfile> getStudent(@PathVariable Long id) {
        return studentProfileRepository.findById(id)
                .filter(s -> !s.isDeleted())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
