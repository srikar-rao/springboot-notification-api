package com.dev.org.service;

import com.dev.org.domain.Notification;
import com.dev.org.domain.NotificationStatus;
import com.dev.org.domain.User;
import com.dev.org.domain.UserNotificationState;
import com.dev.org.mapper.NotificationMapper;
import com.dev.org.model.CreateNotificationRequest;
import com.dev.org.model.NotificationResponse;
import com.dev.org.repository.UserNotificationStateRepository;
import com.dev.org.strategy.FindNotificationStrategy;
import com.dev.org.strategy.NotificationSaveStrategy;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;
    private final List<FindNotificationStrategy> findStrategies;
    private final List<NotificationSaveStrategy> saveStrategies;
    private final UserNotificationStateRepository stateRepository;
    private final SseConnectionManager sseConnectionManager;

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

        NotificationResponse response = notificationMapper.toResponse(saved);

        // Broadcast the created notification asynchronously via SSE
        sseConnectionManager.broadcast(response, targets);

        return response;
    }

    public List<NotificationResponse> getNotifications(String userId, Set<String> roles) {
        Set<String> safeRoles = roles == null ? new HashSet<>() : roles;
        User user = User.builder().id(userId).roles(safeRoles).build();

        // 1. Fetch active notifications from strategies
        List<Notification> activeNotifications =
                findStrategies.stream()
                        .flatMap(strategy -> strategy.getActiveNotifications(user).stream())
                        .distinct()
                        .toList();

        // 2. Fetch user notification states to join read/dismissed status
        Map<String, UserNotificationState> userStates =
                stateRepository.findByUserId(userId).stream()
                        .collect(
                                Collectors.toMap(
                                        UserNotificationState::getNotificationId,
                                        Function.identity()));

        // 3. Map to responses, joining state and filtering out dismissed notifications
        return activeNotifications.stream()
                .filter(
                        n -> {
                            UserNotificationState state = userStates.get(n.getId());
                            return state == null || state.getDismissedAt() == null;
                        })
                .map(n -> notificationMapper.toResponse(n, userStates.get(n.getId())))
                .toList();
    }
}
