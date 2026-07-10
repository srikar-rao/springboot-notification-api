package com.dev.org.listener;

import com.dev.org.event.NotificationCreatedEvent;
import com.dev.org.service.SseConnectionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSseListener {

    private final SseConnectionManager sseConnectionManager;

    @EventListener
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        log.debug(
                "Received NotificationCreatedEvent for notification: {}",
                event.notification().getId());
        sseConnectionManager.broadcast(event.notification(), event.targets());
    }
}
