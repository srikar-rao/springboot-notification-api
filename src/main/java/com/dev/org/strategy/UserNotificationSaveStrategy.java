package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationAudience;
import com.dev.org.repository.NotificationAudienceRepository;
import com.dev.org.repository.NotificationRepository;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserNotificationSaveStrategy implements NotificationSaveStrategy {

    private final NotificationRepository notificationRepository;
    private final NotificationAudienceRepository audienceRepository;
    private final CacheManager cacheManager;

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.USER == audienceType;
    }

    @Override
    public Notification save(Notification notification, Set<String> targets) {
        Notification saved = notificationRepository.save(notification);

        NotificationAudience audience =
                NotificationAudience.builder()
                        .notificationId(saved.getId())
                        .targets(targets)
                        .build();
        audienceRepository.save(audience);

        Cache cache = cacheManager.getCache("notifications");
        if (cache != null && targets != null) {
            log.info("Evicting USER cache for notification: {} with targets: {}", saved.getId(), targets);
            targets.forEach(target -> cache.evict("USER_" + target));
        }

        return saved;
    }
}
