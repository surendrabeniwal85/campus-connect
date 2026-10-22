package com.campusconnect.backend.platform.notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError(e -> removeEmitter(userId, emitter));

        return emitter;
    }

    private void removeEmitter(Long userId, SseEmitter emitter) {
        List<SseEmitter> userEmitters = emitters.get(userId);
        if (userEmitters != null) {
            userEmitters.remove(emitter);
        }
    }

    @Transactional
    public void send(String audience, String targetValue, String title, String body, String link, List<Long> recipientUserIds) {
        // Broadcast via SSE to active open sessions
        for (Long userId : recipientUserIds) {
            List<SseEmitter> userEmitters = emitters.get(userId);
            if (userEmitters != null) {
                for (SseEmitter emitter : userEmitters) {
                    try {
                        emitter.send(SseEmitter.event().name("notification").data(Map.of("title", title, "body", body, "link", link != null ? link : "")));
                    } catch (IOException e) {
                        userEmitters.remove(emitter);
                    }
                }
            }
        }
    }
}