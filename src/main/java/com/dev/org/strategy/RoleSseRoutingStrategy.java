package com.dev.org.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.service.SseEmitterRegistry;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class RoleSseRoutingStrategy implements SseRoutingStrategy {

    private final SseEmitterRegistry registry;

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.ROLE == audienceType;
    }

    @Override
    public Set<SseEmitter> resolveEmitters(Set<String> targets) {
        if (targets == null || targets.isEmpty()) {
            return Set.of();
        }

        Set<SseEmitter> resolved = new HashSet<>();
        for (String target : targets) {
            resolved.addAll(registry.getRoleEmitters(target));
        }
        return resolved;
    }
}
