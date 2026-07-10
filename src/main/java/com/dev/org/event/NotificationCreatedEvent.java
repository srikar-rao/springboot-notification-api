package com.dev.org.event;

import com.dev.org.model.NotificationResponse;
import java.util.Set;

public record NotificationCreatedEvent(NotificationResponse notification, Set<String> targets) {}
