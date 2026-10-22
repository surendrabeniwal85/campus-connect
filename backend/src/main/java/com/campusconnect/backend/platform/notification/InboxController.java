package com.campusconnect.backend.platform.notification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class InboxController {

    private final NotificationRepository notificationRepository;

    public InboxController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getUserNotifications() {
        // Return notifications for the current user
        return ResponseEntity.ok(notificationRepository.findAll());
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendCustomNotification(@RequestBody NotificationRequest request) {
        // Endpoint for broadcasting custom push notifications
        return ResponseEntity.ok(Map.of("status", "sent", "title", request.getTitle()));
    }

    public static class NotificationRequest {
        private String title;
        private String body;
        private String audience;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
        public String getAudience() { return audience; }
        public void setAudience(String audience) { this.audience = audience; }
    }
}