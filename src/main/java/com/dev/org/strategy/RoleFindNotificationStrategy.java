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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoleFindNotificationStrategy implements FindNotificationStrategy {

    private final NotificationRepository notificationRepository;
    private final NotificationAudienceRepository audienceRepository;

    private final ObjectProvider<RoleFindNotificationStrategy> selfProvider;

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.ROLE == audienceType;
    }

    @Override
    public List<Notification> getActiveNotifications(User user) {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty()) {
            return List.of();
        }

        return user.getRoles().stream()
                .flatMap(role -> selfProvider.getObject().getActiveNotificationsForRole(role).stream())
                .toList();
    }

    @Cacheable(value = "notifications", key = "'ROLE_' + #role")
    public List<Notification> getActiveNotificationsForRole(String role) {
        List<String> notificationIds =
                audienceRepository.findByTargetsContaining(role).stream()
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
