package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import java.util.Set;

public interface NotificationSaveStrategy {

    boolean supports(AudienceType audienceType);

    Notification save(Notification notification, Set<String> targets);
}
