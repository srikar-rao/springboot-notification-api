package com.dev.org.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User-specific notification state (Read/Dismissed tracking).
 * Modeled separately because global and role-based notifications are shared.
 */
@Document(collection = "user_notification_states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotificationState {
    @Id private String id;

    private String notificationId;

    private String userId;

    /**
     * Missing readAt date means the notification is unread.
     */
    private Instant readAt;

    /**
     * Missing dismissedAt date means the notification has not been dismissed.
     */
    private Instant dismissedAt;

    private Instant createdAt;
}
