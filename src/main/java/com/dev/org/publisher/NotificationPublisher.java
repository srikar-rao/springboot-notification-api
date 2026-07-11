package com.dev.org.publisher;

import com.dev.org.event.NotificationCreatedEvent;
import com.dev.org.model.NotificationResponse;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishNotificationCreated(NotificationResponse notification, Set<String> targets) {
        eventPublisher.publishEvent(new NotificationCreatedEvent(notification, targets));
    }
}
