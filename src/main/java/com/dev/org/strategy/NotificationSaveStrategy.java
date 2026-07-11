package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;

public interface NotificationSaveStrategy {

    boolean supports(AudienceType audienceType);

    Notification save(Notification notification);
}
