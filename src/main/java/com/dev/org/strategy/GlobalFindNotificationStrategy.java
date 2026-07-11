package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.domain.User;
import com.dev.org.repository.NotificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalFindNotificationStrategy implements FindNotificationStrategy {

    private final NotificationRepository notificationRepository;

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.GLOBAL == audienceType;
    }

    @Override
    @Cacheable(value = "notifications", key = "'GLOBAL'")
    public List<Notification> getActiveNotifications(User user) {
        return notificationRepository
                .findByAudienceTypeOrderByUpdatedAtDesc(AudienceType.GLOBAL)
                .stream()
                .filter(n -> n.getStatus() == NotificationStatus.ACTIVE)
                .toList();
    }
}
