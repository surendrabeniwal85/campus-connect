package com.campusconnect.backend.placement.selection.candidate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/rounds")
public class CandidateController {

    private final CandidateStatusService statusService;

    public CandidateController(CandidateStatusService statusService) {
        this.statusService = statusService;
    }

    @PostMapping("/{roundId}/shortlist")
    public ResponseEntity<?> shortlistCandidate(@PathVariable Long roundId, @RequestParam Long candidateId) {
        try {
            RoundCandidate updated = statusService.updateStatus(candidateId, RoundCandidate.CandidateStatus.SHORTLISTED);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{roundId}/advance")
    public ResponseEntity<?> advanceCandidate(@PathVariable Long roundId, @RequestParam Long candidateId) {
        try {
            RoundCandidate updated = statusService.updateStatus(candidateId, RoundCandidate.CandidateStatus.ADVANCED);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{roundId}/eliminate")
    public ResponseEntity<?> eliminateCandidate(@PathVariable Long roundId, @RequestParam Long candidateId) {
        try {
            RoundCandidate updated = statusService.updateStatus(candidateId, RoundCandidate.CandidateStatus.ELIMINATED);
            return ResponseEntity.ok(updated);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        }
    }
}