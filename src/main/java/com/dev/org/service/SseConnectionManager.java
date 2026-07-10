package com.dev.org.service;

import com.dev.org.domain.AudienceType;
import com.dev.org.domain.User;
import com.dev.org.model.NotificationResponse;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
public class SseConnectionManager {

    // 1 hour timeout
    private static final long SSE_TIMEOUT = 3600000L;

    // Use Virtual Threads for sending events concurrently without exhausting thread pools
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    // Scheduler for debouncing notifications (runs the timer only)
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    // Data structures for routing
    private final ConcurrentMap<String, Set<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<SseEmitter>> roleEmitters = new ConcurrentHashMap<>();
    private final Set<SseEmitter> globalEmitters = new CopyOnWriteArraySet<>();
    private final ConcurrentMap<SseEmitter, ScheduledFuture<?>> pendingRefreshes =
            new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
        scheduler.shutdownNow();
    }

    public SseEmitter subscribe(User user) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        String userId = user.getId();
        Set<String> roles = user.getRoles();

        // Register for user-specific events
        if (userId != null && !userId.isEmpty()) {
            userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(emitter);
        }

        // Register for role-specific events
        if (roles != null) {
            for (String role : roles) {
                if (role != null && !role.isEmpty()) {
                    roleEmitters
                            .computeIfAbsent(role, k -> new CopyOnWriteArraySet<>())
                            .add(emitter);
                }
            }
        }

        // Register for global events
        globalEmitters.add(emitter);

        // Cleanup callbacks
        Runnable cleanup = () -> removeEmitter(emitter, userId, roles);
        emitter.onCompletion(cleanup);
        emitter.onTimeout(cleanup);
        emitter.onError(
                e -> {
                    log.error("SSE Error for user {}: {}", userId, e.getMessage());
                    cleanup.run();
                });

        // Send an initial connected event
        try {
            emitter.send(SseEmitter.event().name("connected").data("Connection established"));
        } catch (IOException e) {
            log.warn("Failed to send initial connected event to user {}", userId);
            emitter.completeWithError(e);
        }

        log.info(
                "New SSE connection for user {}. Total global connections: {}",
                userId,
                globalEmitters.size());
        return emitter;
    }

    public void broadcast(NotificationResponse notification, Set<String> targets) {
        executor.submit(
                () -> {
                    AudienceType type = AudienceType.valueOf(notification.getAudienceType().name());

                    switch (type) {
                        case GLOBAL:
                            scheduleRefresh(globalEmitters);
                            break;
                        case USER:
                            if (targets != null) {
                                for (String userId : targets) {
                                    Set<SseEmitter> emitters = userEmitters.get(userId);
                                    if (emitters != null) {
                                        scheduleRefresh(emitters);
                                    }
                                }
                            }
                            break;
                        case ROLE:
                            if (targets != null) {
                                for (String role : targets) {
                                    Set<SseEmitter> emitters = roleEmitters.get(role);
                                    if (emitters != null) {
                                        scheduleRefresh(emitters);
                                    }
                                }
                            }
                            break;
                        default:
                            log.warn("Unknown audience type: {}", type);
                            break;
                    }
                });
    }

    private void scheduleRefresh(Set<SseEmitter> emitters) {
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            pendingRefreshes.compute(
                    emitter,
                    (e, future) -> {
                        // If a refresh is already scheduled, cancel it to reset the timer
                        if (future != null) {
                            future.cancel(false);
                        }

                        // Schedule a new refresh 2 seconds from now
                        return scheduler.schedule(
                                () -> {
                                    pendingRefreshes.remove(e);

                                    // Dispatch the actual network I/O to a virtual thread
                                    executor.submit(
                                            () -> {
                                                try {
                                                    e.send(
                                                            SseEmitter.event()
                                                                    .name("refresh")
                                                                    .data("NEW_NOTIFICATION"));
                                                } catch (IOException ex) {
                                                    log.debug(
                                                            "Failed to send refresh signal,"
                                                                    + " completing emitter",
                                                            ex);
                                                    e.complete();
                                                }
                                            });
                                },
                                2,
                                TimeUnit.SECONDS);
                    });
        }
    }

    private void removeEmitter(SseEmitter emitter, String userId, Set<String> roles) {
        ScheduledFuture<?> future = pendingRefreshes.remove(emitter);
        if (future != null) {
            future.cancel(false);
        }

        globalEmitters.remove(emitter);

        if (userId != null && !userId.isEmpty()) {
            Set<SseEmitter> uEmitters = userEmitters.get(userId);
            if (uEmitters != null) {
                uEmitters.remove(emitter);
                if (uEmitters.isEmpty()) {
                    userEmitters.remove(userId);
                }
            }
        }

        if (roles != null) {
            for (String role : roles) {
                if (role != null && !role.isEmpty()) {
                    Set<SseEmitter> rEmitters = roleEmitters.get(role);
                    if (rEmitters != null) {
                        rEmitters.remove(emitter);
                        if (rEmitters.isEmpty()) {
                            roleEmitters.remove(role);
                        }
                    }
                }
            }
        }
    }
}
