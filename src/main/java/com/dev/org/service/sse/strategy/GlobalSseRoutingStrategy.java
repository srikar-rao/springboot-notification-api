package com.dev.org.service.sse.strategy;

import com.dev.org.domain.AudienceType;
import com.dev.org.service.sse.SseEmitterRegistry;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Component
@RequiredArgsConstructor
public class GlobalSseRoutingStrategy implements SseRoutingStrategy {

    private final SseEmitterRegistry registry;

    @Override
    public boolean supports(AudienceType audienceType) {
        return AudienceType.GLOBAL == audienceType;
    }

    @Override
    public Set<SseEmitter> resolveEmitters(Set<String> targets) {
        return registry.getGlobalEmitters();
    }
}
