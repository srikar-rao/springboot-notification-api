package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import java.util.List;
import java.util.Set;

public interface NotificationStrategy {

    AudienceType getAudienceType();

    List<Notification> getActiveNotifications(Set<String> targets);
}
