package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.User;
import java.util.List;

public interface NotificationStrategy {

    boolean supports(AudienceType audienceType);

    List<Notification> getActiveNotifications(User user);
}
