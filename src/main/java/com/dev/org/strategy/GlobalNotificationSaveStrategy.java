package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.repository.NotificationAudienceRepository;
import com.dev.org.repository.NotificationRepository;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalNotificationSaveStrategy extends AbstractNotificationSaveStrategy {

    public GlobalNotificationSaveStrategy(
            NotificationRepository notificationRepository,
            NotificationAudienceRepository audienceRepository,
            CacheManager cacheManager) {
        super(notificationRepository, audienceRepository, cacheManager);
    }

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.GLOBAL == audienceType;
    }

    @Override
    protected void invalidateCache(Notification notification, Set<String> targets) {
        Cache cache = cacheManager.getCache("notifications");
        if (cache != null) {
            log.info("Evicting GLOBAL cache for notification: {}", notification.getId());
            cache.evict("GLOBAL");
        }
    }
}
