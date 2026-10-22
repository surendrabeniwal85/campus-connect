package com.campusconnect.backend.placement.selection.candidate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class FeedbackController {

    private final FeedbackRepository repository;

    public FeedbackController(FeedbackRepository repository) {
        this.repository = repository;
    }

    @PostMapping("/feedback")
    public ResponseEntity<DriveFeedback> submitFeedback(@RequestBody DriveFeedback feedback) {
        DriveFeedback saved = repository.save(feedback);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/drives/{id}/feedback/summary")
    public ResponseEntity<?> getFeedbackSummary(@PathVariable Long id) {
        List<DriveFeedback> list = repository.findByDriveId(id);
        double avgRating = list.stream().mapToInt(DriveFeedback::getRating).average().orElse(0.0);
        return ResponseEntity.ok(Map.of(
            "driveId", id,
            "totalFeedback", list.size(),
            "averageRating", avgRating,
            "feedbacks", list
        ));
    }
}