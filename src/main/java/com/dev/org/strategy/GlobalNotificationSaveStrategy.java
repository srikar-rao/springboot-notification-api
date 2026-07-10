package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.repository.NotificationRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GlobalNotificationSaveStrategy implements NotificationSaveStrategy {

    private final NotificationRepository notificationRepository;
    private final CacheManager cacheManager;

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.GLOBAL == audienceType;
    }

    @Override
    public Notification save(Notification notification, Set<String> targets) {
        Notification saved = notificationRepository.save(notification);

        Cache cache = cacheManager.getCache("notifications");
        if (cache != null) {
            cache.evict("GLOBAL");
        }

        return saved;
    }
}
