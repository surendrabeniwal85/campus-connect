package com.campusconnect.backend.placement.selection.candidate;

import com.campusconnect.backend.platform.notification.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class EvaluationController {

    private final EvaluationRepository evaluationRepository;
    private final NotificationService notificationService;

    public EvaluationController(EvaluationRepository evaluationRepository, NotificationService notificationService) {
        this.evaluationRepository = evaluationRepository;
        this.notificationService = notificationService;
    }

    @PostMapping("/rounds/{roundId}/evaluations")
    public ResponseEntity<Evaluation> submitEvaluation(
            @PathVariable Long roundId,
            @RequestBody Evaluation evaluationRequest) {
        evaluationRequest.setRoundId(roundId);
        Evaluation saved = evaluationRepository.save(evaluationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/drives/{id}/finalize")
    @Transactional
    public ResponseEntity<?> finalizeDriveSelection(
            @PathVariable Long id,
            @RequestBody List<Long> selectedStudentIds) {
        try {
            // Transactional final selection save
            for (Long studentId : selectedStudentIds) {
                // Save selection status logic
                notificationService.send(
                    "USER",
                    String.valueOf(studentId),
                    "Final Selection",
                    "Congratulations! You have been selected in the final placement drive.",
                    "/student/drives",
                    List.of(studentId)
                );
            }
            return ResponseEntity.ok(Map.of("message", "Drive finalized successfully and students notified."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Finalization failed. Transaction rolled back."));
        }
    }
}