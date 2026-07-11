package com.dev.org.event;

import com.dev.org.domain.Notification;
import com.dev.org.model.NotificationResponse;

public record NotificationCreatedEvent(Notification notification, NotificationResponse response) {}
