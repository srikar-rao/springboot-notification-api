package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationAudience;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.domain.User;
import com.dev.org.repository.NotificationAudienceRepository;
import com.dev.org.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFindNotificationStrategy implements FindNotificationStrategy {

    private final NotificationRepository notificationRepository;
    private final NotificationAudienceRepository audienceRepository;

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.USER == audienceType;
    }

    @Override
    @Cacheable(value = "notifications", key = "'USER_' + #user.id")
    public List<Notification> getActiveNotifications(User user) {
        if (user == null || user.getId() == null) {
            return List.of();
        }

        List<String> notificationIds =
                audienceRepository.findByTargetsContaining(user.getId()).stream()
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
