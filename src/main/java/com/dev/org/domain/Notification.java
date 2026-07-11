package com.dev.org.domain;

import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Main Notification content model.
 */
@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    @Id private String id;

    private String title;

    private String message;

    private String actionUrl;

    private String type;

    private NotificationPriority priority;

    private AudienceType audienceType;

    private NotificationSeverity severity;

    private NotificationStatus status;

    /**
     * Identifiers of the targets (User IDs or Role names/IDs).
     * For GLOBAL notifications, this should be empty/null.
     */
    private Set<String> targets;

    private Instant publishedAt;

    private Instant expiresAt;

    private Instant createdAt;

    private Instant updatedAt;
}
