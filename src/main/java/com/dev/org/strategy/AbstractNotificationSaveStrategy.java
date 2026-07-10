package com.dev.org.strategy;

import com.dev.org.domain.Notification;
import com.dev.org.repository.NotificationAudienceRepository;
import com.dev.org.repository.NotificationRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractNotificationSaveStrategy implements NotificationSaveStrategy {

    protected final NotificationRepository notificationRepository;
    protected final NotificationAudienceRepository audienceRepository;
    protected final CacheManager cacheManager;

    @Override
    public final Notification save(Notification notification, Set<String> targets) {
        Notification saved = saveNotification(notification);
        saveAudience(saved, targets);
        invalidateCache(saved, targets);
        return saved;
    }

    protected Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    protected void saveAudience(Notification notification, Set<String> targets) {
        // Default implementation - empty for GLOBAL notifications
    }

    protected abstract void invalidateCache(Notification notification, Set<String> targets);
}
