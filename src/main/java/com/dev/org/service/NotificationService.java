package com.dev.org.service;

import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.domain.User;
import com.dev.org.mapper.NotificationMapper;
import com.dev.org.model.CreateNotificationRequest;
import com.dev.org.model.NotificationResponse;
import com.dev.org.strategy.FindNotificationStrategy;
import com.dev.org.strategy.NotificationSaveStrategy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final List<FindNotificationStrategy> findStrategies;
    private final List<NotificationSaveStrategy> saveStrategies;

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

        Notification saved =
                saveStrategies.stream()
                        .filter(strategy -> strategy.supports(notification.getAudienceType()))
                        .findFirst()
                        .map(strategy -> strategy.save(notification, targets))
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "No save strategy found for audience type: "
                                                        + notification.getAudienceType()));

        return notificationMapper.toResponse(saved);
    }

    public List<NotificationResponse> getNotifications(String userId, Set<String> roles) {
        Set<String> safeRoles = roles == null ? new HashSet<>() : roles;
        User user = User.builder().id(userId).roles(safeRoles).build();

        return findStrategies.stream()
                .flatMap(strategy -> strategy.getActiveNotifications(user).stream())
                .distinct()
                .map(notificationMapper::toResponse)
                .toList();
    }
}
