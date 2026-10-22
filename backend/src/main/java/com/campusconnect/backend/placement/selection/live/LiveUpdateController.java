package com.campusconnect.backend.placement.selection.candidate;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/drives")
public class LiveUpdateController {

    private final LiveUpdateRepository repository;

    public LiveUpdateController(LiveUpdateRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}/live")
    public ResponseEntity<List<LiveSessionUpdate>> getLiveUpdates(@PathVariable Long id) {
        return ResponseEntity.ok(repository.findByDriveId(id));
    }

    @PostMapping("/{id}/live")
    public ResponseEntity<LiveSessionUpdate> postLiveUpdate(
            @PathVariable Long id,
            @RequestBody LiveSessionUpdate update) {
        update.setDriveId(id);
        LiveSessionUpdate saved = repository.save(update);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}