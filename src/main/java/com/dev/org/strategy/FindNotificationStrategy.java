package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.User;
import java.util.List;

public interface FindNotificationStrategy {

    boolean supports(AudienceType audienceType);

    List<Notification> getActiveNotifications(User user);
}
