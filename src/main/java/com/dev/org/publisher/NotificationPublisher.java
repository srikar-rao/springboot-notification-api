package com.dev.org.publisher;

import com.dev.org.domain.Notification;
import com.dev.org.event.NotificationCreatedEvent;
import com.dev.org.model.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishNotificationCreated(
            Notification notification, NotificationResponse response) {
        eventPublisher.publishEvent(new NotificationCreatedEvent(notification, response));
    }
}
