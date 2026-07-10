package com.dev.org.repository;

import com.dev.org.domain.NotificationAudience;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationAudienceRepository
        extends MongoRepository<NotificationAudience, String> {
    Optional<NotificationAudience> findByNotificationId(String notificationId);

    List<NotificationAudience> findByTargetsContaining(String target);

    List<NotificationAudience> findByTargetsIn(Set<String> targets);
}
