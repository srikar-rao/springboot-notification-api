package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.repository.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserNotificationSaveStrategy extends AbstractNotificationSaveStrategy {

    public UserNotificationSaveStrategy(
            NotificationRepository notificationRepository, CacheManager cacheManager) {
        super(notificationRepository, cacheManager);
    }

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.USER == audienceType;
    }

    @Override
    protected void invalidateCache(Notification notification) {
        Cache cache = cacheManager.getCache("notifications");
        if (cache != null && notification.getTargets() != null) {
            log.info(
                    "Evicting USER cache for notification: {} with targets: {}",
                    notification.getId(),
                    notification.getTargets());
            notification.getTargets().forEach(target -> cache.evict("USER_" + target));
        }
    }
}
