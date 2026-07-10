package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.repository.NotificationRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalNotificationStrategy implements NotificationStrategy {

    private final NotificationRepository notificationRepository;

    @Override
    public AudienceType getAudienceType() {
        return AudienceType.GLOBAL;
    }

    @Override
    @Cacheable(value = "notifications", key = "'GLOBAL'")
    public List<Notification> getActiveNotifications(Set<String> targets) {
        return notificationRepository.findByAudienceType(AudienceType.GLOBAL).stream()
                .filter(n -> n.getStatus() == NotificationStatus.ACTIVE)
                .toList();
    }
}
