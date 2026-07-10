package com.dev.org.repository;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB Repository for Notification.
 */
@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByAudienceType(AudienceType audienceType);
}
