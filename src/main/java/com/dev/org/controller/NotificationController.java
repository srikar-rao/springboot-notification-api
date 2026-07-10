package com.dev.org.controller;

import com.dev.org.controller.api.NotificationsApi;
import com.dev.org.model.NotificationResponse;
import com.dev.org.service.NotificationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController implements NotificationsApi {

    private final NotificationService notificationService;

    @Override
    public ResponseEntity<List<NotificationResponse>> findAllNotifications() {
        return ResponseEntity.ok(notificationService.findAllNotifications());
    }
}
