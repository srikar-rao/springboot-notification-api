package com.dev.org.publisher;

import com.dev.org.domain.Notification;
import com.dev.org.event.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishNotificationCreated(Notification notification) {
        eventPublisher.publishEvent(new NotificationCreatedEvent(notification));
    }
}
