package com.dev.org.service;

import com.dev.org.domain.User;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
public class SseEmitterRegistry {
    private final ConcurrentMap<String, Set<SseEmitter>> userEmitters = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<SseEmitter>> roleEmitters = new ConcurrentHashMap<>();
    private final Set<SseEmitter> globalEmitters = new CopyOnWriteArraySet<>();

    public void register(SseEmitter emitter, User user) {
        String userId = user.getId();
        Set<String> roles = user.getRoles();

        if (userId != null && !userId.isEmpty()) {
            userEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(emitter);
        }

        if (roles != null) {
            for (String role : roles) {
                if (role != null && !role.isEmpty()) {
                    roleEmitters
                            .computeIfAbsent(role, k -> new CopyOnWriteArraySet<>())
                            .add(emitter);
                }
            }
        }

        globalEmitters.add(emitter);
    }

    public void unregister(SseEmitter emitter, User user) {
        String userId = user.getId();
        Set<String> roles = user.getRoles();

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

    public Set<SseEmitter> getGlobalEmitters() {
        return globalEmitters;
    }

    public Set<SseEmitter> getUserEmitters(String userId) {
        return userEmitters.getOrDefault(userId, Set.of());
    }

    public Set<SseEmitter> getRoleEmitters(String role) {
        return roleEmitters.getOrDefault(role, Set.of());
    }
}
