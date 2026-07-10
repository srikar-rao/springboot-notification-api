package com.dev.org.mapper;

import com.dev.org.domain.Notification;
import com.dev.org.model.CreateNotificationRequest;
import com.dev.org.model.NotificationResponse;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toDomain(CreateNotificationRequest request) {
        if (request == null) {
            return null;
        }

        return Notification.builder()
                .title(request.getTitle())
                .message(request.getMessage())
                .actionUrl(request.getActionUrl())
                .type(request.getType())
                .priority(
                        request.getPriority() != null
                                ? com.dev.org.domain.NotificationPriority.valueOf(
                                        request.getPriority().name())
                                : null)
                .audienceType(
                        request.getAudienceType() != null
                                ? com.dev.org.domain.AudienceType.valueOf(
                                        request.getAudienceType().name())
                                : null)
                .severity(
                        request.getSeverity() != null
                                ? com.dev.org.domain.NotificationSeverity.valueOf(
                                        request.getSeverity().name())
                                : com.dev.org.domain.NotificationSeverity.INFO)
                .build();
    }

    public NotificationResponse toResponse(Notification notification) {
        return toResponse(notification, null);
    }

    public NotificationResponse toResponse(
            Notification notification, com.dev.org.domain.UserNotificationState state) {
        if (notification == null) {
            return null;
        }

        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setActionUrl(notification.getActionUrl());
        response.setType(notification.getType());

        if (notification.getPriority() != null) {
            response.setPriority(
                    NotificationResponse.PriorityEnum.valueOf(notification.getPriority().name()));
        }
        if (notification.getAudienceType() != null) {
            response.setAudienceType(
                    NotificationResponse.AudienceTypeEnum.valueOf(
                            notification.getAudienceType().name()));
        }
        if (notification.getSeverity() != null) {
            response.setSeverity(
                    NotificationResponse.SeverityEnum.valueOf(notification.getSeverity().name()));
        }
        if (notification.getStatus() != null) {
            response.setStatus(
                    NotificationResponse.StatusEnum.valueOf(notification.getStatus().name()));
        }

        if (notification.getPublishedAt() != null) {
            response.setPublishedAt(
                    OffsetDateTime.ofInstant(notification.getPublishedAt(), ZoneOffset.UTC));
        }
        if (notification.getExpiresAt() != null) {
            response.setExpiresAt(
                    OffsetDateTime.ofInstant(notification.getExpiresAt(), ZoneOffset.UTC));
        }
        if (notification.getCreatedAt() != null) {
            response.setCreatedAt(
                    OffsetDateTime.ofInstant(notification.getCreatedAt(), ZoneOffset.UTC));
        }
        if (notification.getUpdatedAt() != null) {
            response.setUpdatedAt(
                    OffsetDateTime.ofInstant(notification.getUpdatedAt(), ZoneOffset.UTC));
        }

        if (state != null) {
            if (state.getReadAt() != null) {
                response.setReadAt(OffsetDateTime.ofInstant(state.getReadAt(), ZoneOffset.UTC));
            }
            if (state.getDismissedAt() != null) {
                response.setDismissedAt(
                        OffsetDateTime.ofInstant(state.getDismissedAt(), ZoneOffset.UTC));
            }
        }

        return response;
    }
}
