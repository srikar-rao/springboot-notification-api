package com.dev.org.domain;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Separated audience configuration from the main notification.
 * This stores the actual targets (userIds or roleIds) for USER and ROLE audience types.
 */
@Document(collection = "notification_audiences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationAudience {
    @Id private String id;

    private String notificationId;

    /**
     * Identifiers of the targets (User IDs or Role names/IDs).
     * For GLOBAL notifications, this should be empty/null.
     */
    private Set<String> targets;
}
