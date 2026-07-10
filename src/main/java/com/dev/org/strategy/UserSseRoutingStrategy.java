package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;

import java.util.HashSet;
import java.util.Set;

import com.dev.org.service.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class UserSseRoutingStrategy implements SseRoutingStrategy {

    private final SseEmitterRegistry registry;

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.USER == audienceType;
    }

    @Override
    public Set<SseEmitter> resolveEmitters(Set<String> targets) {
        if (targets == null || targets.isEmpty()) {
            return Set.of();
        }

        Set<SseEmitter> resolved = new HashSet<>();
        for (String target : targets) {
            resolved.addAll(registry.getUserEmitters(target));
        }
        return resolved;
    }
}
