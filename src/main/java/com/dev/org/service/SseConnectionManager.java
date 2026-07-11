package com.dev.org.service;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.Notification;
import com.dev.org.domain.User;
import com.dev.org.model.NotificationResponse;
import com.dev.org.strategy.SseRoutingStrategy;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Facade for orchestrating SSE connections.
 * Uses SseEmitterRegistry to store connections, SseEventDispatcher to send events asynchronously,
 * and SseRoutingStrategy implementations to route events based on AudienceType.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SseConnectionManager {

    private static final long SSE_TIMEOUT = 3600000L;

    private final SseEmitterRegistry registry;
    private final SseEventDispatcher dispatcher;
    private final List<SseRoutingStrategy> routingStrategies;

    @PostConstruct
    public void init() {
        // Start periodic heartbeat to detect and clean up dropped client connections
        dispatcher.startHeartbeat(registry.getGlobalEmitters());
    }

    public SseEmitter subscribe(User user) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        registry.register(emitter, user);

        Runnable cleanup =
                () -> {
                    dispatcher.cancelPending(emitter);
                    registry.unregister(emitter, user);
                    log.info(
                            "SSE connection closed for user {}. Total global connections: {}",
                            user.getId(),
                            registry.getGlobalEmitters().size());
                };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(
                e -> {
                    log.error("SSE Error for user {}: {}", user.getId(), e.getMessage());
                    cleanup.run();
                });

        dispatcher.dispatchConnected(emitter);

        log.info(
                "New SSE connection for user {}. Total global connections: {}",
                user.getId(),
                registry.getGlobalEmitters().size());
        return emitter;
    }

    public void broadcast(Notification notification, NotificationResponse response) {
        AudienceType type = notification.getAudienceType();
        Set<String> targets = notification.getTargets();

        routingStrategies.stream()
                .filter(strategy -> strategy.supports(type))
                .findFirst()
                .ifPresentOrElse(
                        strategy -> {
                            Set<SseEmitter> emitters = strategy.resolveEmitters(targets);
                            dispatcher.dispatchNotification(emitters, response);
                        },
                        () ->
                                log.warn(
                                        "No SSE routing strategy found for audience type: {}",
                                        type));
    }
}
