package com.dev.org.controller;

import com.dev.org.controller.api.NotificationsApi;
import com.dev.org.domain.User;
import com.dev.org.model.CreateNotificationRequest;
import com.dev.org.model.NotificationResponse;
import com.dev.org.service.NotificationService;
import com.dev.org.service.SseConnectionManager;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationsApi {

    private final NotificationService notificationService;
    private final SseConnectionManager sseConnectionManager;

    @Override
    public ResponseEntity<NotificationResponse> createNotification(
            CreateNotificationRequest request) {
        NotificationResponse response = notificationService.createNotification(request);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(response.getId())
                        .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            String userId, List<String> roles) {
        java.util.Set<String> rolesSet =
                roles == null ? new java.util.HashSet<>() : new java.util.HashSet<>(roles);
        return ResponseEntity.ok(notificationService.getNotifications(userId, rolesSet));
    }

    @GetMapping(
            value = "/notifications/stream",
            produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(
            @RequestParam String userId, @RequestParam(required = false) List<String> roles) {
        java.util.Set<String> rolesSet =
                roles == null ? new java.util.HashSet<>() : new java.util.HashSet<>(roles);

        User user = User.builder().id(userId).roles(rolesSet).build();

        return sseConnectionManager.subscribe(user);
    }
}
