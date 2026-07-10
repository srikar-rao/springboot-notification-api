package com.dev.org.service.sse;

import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Component
public class SseEventDispatcher {

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentMap<SseEmitter, ScheduledFuture<?>> pendingRefreshes =
            new ConcurrentHashMap<>();

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
        scheduler.shutdownNow();
    }

    public void startHeartbeat(Set<SseEmitter> emitters) {
        scheduler.scheduleAtFixedRate(
                () -> {
                    if (emitters == null || emitters.isEmpty()) {
                        return;
                    }
                    for (SseEmitter emitter : emitters) {
                        executor.submit(
                                () -> {
                                    try {
                                        emitter.send(
                                                SseEmitter.event().name("ping").data("keep-alive"));
                                    } catch (IOException e) {
                                        emitter.completeWithError(e);
                                    }
                                });
                    }
                },
                5,
                10,
                TimeUnit.SECONDS);
    }

    public void dispatchRefresh(Set<SseEmitter> emitters) {
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        for (SseEmitter emitter : emitters) {
            pendingRefreshes.compute(
                    emitter,
                    (e, future) -> {
                        if (future != null) {
                            future.cancel(false);
                        }

                        return scheduler.schedule(
                                () -> {
                                    pendingRefreshes.remove(e);
                                    executor.submit(() -> sendRefresh(e));
                                },
                                2,
                                TimeUnit.SECONDS);
                    });
        }
    }

    private void sendRefresh(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("refresh").data("NEW_NOTIFICATION"));
        } catch (IOException ex) {
            log.debug("Failed to send refresh signal, completing emitter", ex);
            emitter.completeWithError(ex);
        }
    }

    public void dispatchConnected(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().name("connected").data("Connection established"));
        } catch (IOException e) {
            log.warn("Failed to send initial connected event");
            emitter.completeWithError(e);
        }
    }

    public void cancelPending(SseEmitter emitter) {
        ScheduledFuture<?> future = pendingRefreshes.remove(emitter);
        if (future != null) {
            future.cancel(false);
        }
    }
}
