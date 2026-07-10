package com.dev.org.service.sse.strategy;

import com.dev.org.domain.AudienceType;
import java.util.Set;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseRoutingStrategy {
    boolean supports(AudienceType audienceType);

    Set<SseEmitter> resolveEmitters(Set<String> targets);
}
