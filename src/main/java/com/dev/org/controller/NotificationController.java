package com.dev.org.controller;

import com.dev.org.controller.api.NotificationsApi;
import com.dev.org.model.CreateNotificationRequest;
import com.dev.org.model.NotificationResponse;
import com.dev.org.service.NotificationService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationsApi {

    private final NotificationService notificationService;

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
}
