package com.dev.org.service;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationAudience;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.mapper.NotificationMapper;
import com.dev.org.model.CreateNotificationRequest;
import com.dev.org.model.NotificationResponse;
import com.dev.org.repository.NotificationAudienceRepository;
import com.dev.org.repository.NotificationRepository;
import com.dev.org.strategy.GlobalNotificationStrategy;
import com.dev.org.strategy.TargetedNotificationStrategy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationAudienceRepository audienceRepository;
    private final NotificationMapper notificationMapper;
    private final GlobalNotificationStrategy globalStrategy;
    private final TargetedNotificationStrategy targetedStrategy;
    private final CacheManager cacheManager;

    public NotificationResponse createNotification(CreateNotificationRequest request) {
        Set<String> targets =
                request.getTargets() == null
                        ? new HashSet<>()
                        : new HashSet<>(request.getTargets());

        Notification notification = notificationMapper.toDomain(request);
        notification.setStatus(NotificationStatus.ACTIVE); // For simplicity, we active immediately
        Instant now = Instant.now();
        notification.setCreatedAt(now);
        notification.setUpdatedAt(now);
        notification.setPublishedAt(now);
        notification.setExpiresAt(now.plus(30, ChronoUnit.DAYS));

        Notification saved = notificationRepository.save(notification);

        if (notification.getAudienceType() != AudienceType.GLOBAL) {
            NotificationAudience audience =
                    NotificationAudience.builder()
                            .notificationId(saved.getId())
                            .targets(targets)
                            .build();
            audienceRepository.save(audience);

            // Invalidate targeted cache entries
            Cache cache = cacheManager.getCache("notifications");
            if (cache != null) {
                targets.forEach(cache::evict);
            }
        } else {
            // Invalidate global cache entry
            Cache cache = cacheManager.getCache("notifications");
            if (cache != null) {
                cache.evict("GLOBAL");
            }
        }

        return notificationMapper.toResponse(saved);
    }

    public List<NotificationResponse> getNotifications(String userId, Set<String> roles) {
        Set<String> safeRoles = roles == null ? new HashSet<>() : roles;
        Set<String> targetIds = new HashSet<>(safeRoles);
        targetIds.add(userId);

        List<Notification> activeNotifications = new ArrayList<>();

        // GLOBAL (Uses cached strategy)
        activeNotifications.addAll(globalStrategy.getActiveNotifications(null));

        // TARGETS (Uses cached strategy)
        for (String target : targetIds) {
            activeNotifications.addAll(targetedStrategy.getActiveNotificationsForTarget(target));
        }

        return activeNotifications.stream()
                // Ensure no duplicates if a notification hit multiple targets
                .distinct()
                .map(notificationMapper::toResponse)
                .toList();
    }
}
