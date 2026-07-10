package com.dev.org.repository;

import com.dev.org.entity.NotificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB Repository for NotificationEntity.
 */
@Repository
public interface NotificationRepository extends MongoRepository<NotificationEntity, String> {}
