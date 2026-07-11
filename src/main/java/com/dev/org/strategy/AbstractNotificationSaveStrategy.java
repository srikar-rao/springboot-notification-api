package com.dev.org.strategy;

import com.dev.org.domain.Notification;
import com.dev.org.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractNotificationSaveStrategy implements NotificationSaveStrategy {

    protected final NotificationRepository notificationRepository;
    protected final CacheManager cacheManager;

    @Override
    public final Notification save(Notification notification) {
        Notification saved = saveNotification(notification);
        invalidateCache(saved);
        return saved;
    }

    protected Notification saveNotification(Notification notification) {
        return notificationRepository.save(notification);
    }

    protected abstract void invalidateCache(Notification notification);
}
