package com.dev.org.strategy;

import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationAudience;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.repository.NotificationAudienceRepository;
import com.dev.org.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TargetedNotificationStrategy {

    private final NotificationRepository notificationRepository;
    private final NotificationAudienceRepository audienceRepository;

    @Cacheable(value = "notifications", key = "#target")
    public List<Notification> getActiveNotificationsForTarget(String target) {
        List<String> notificationIds =
                audienceRepository.findByTargetsContaining(target).stream()
                        .map(NotificationAudience::getNotificationId)
                        .toList();

        if (notificationIds.isEmpty()) {
            return List.of();
        }

        return notificationRepository.findAllById(notificationIds).stream()
                .filter(n -> n.getStatus() == NotificationStatus.ACTIVE)
                .toList();
    }
}
