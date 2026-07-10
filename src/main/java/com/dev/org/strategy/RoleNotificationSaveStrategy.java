package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationAudience;
import com.dev.org.repository.NotificationAudienceRepository;
import com.dev.org.repository.NotificationRepository;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoleNotificationSaveStrategy extends AbstractNotificationSaveStrategy {

    public RoleNotificationSaveStrategy(
            NotificationRepository notificationRepository,
            NotificationAudienceRepository audienceRepository,
            CacheManager cacheManager) {
        super(notificationRepository, audienceRepository, cacheManager);
    }

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.ROLE == audienceType;
    }

    @Override
    protected void saveAudience(Notification notification, Set<String> targets) {
        if (targets == null || targets.isEmpty()) {
            return;
        }
        NotificationAudience audience =
                NotificationAudience.builder()
                        .notificationId(notification.getId())
                        .targets(targets)
                        .build();
        audienceRepository.save(audience);
    }

    @Override
    protected void invalidateCache(Notification notification, Set<String> targets) {
        Cache cache = cacheManager.getCache("notifications");
        if (cache != null && targets != null) {
            log.info(
                    "Evicting ROLE cache for notification: {} with targets: {}",
                    notification.getId(),
                    targets);
            targets.forEach(target -> cache.evict("ROLE_" + target));
        }
    }
}
