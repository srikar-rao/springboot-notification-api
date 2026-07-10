package com.dev.org.repository;

import com.dev.org.domain.UserNotificationState;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationStateRepository
        extends MongoRepository<UserNotificationState, String> {
    Optional<UserNotificationState> findByNotificationIdAndUserId(
            String notificationId, String userId);

    List<UserNotificationState> findByUserId(String userId);
}
