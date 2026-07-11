package com.dev.org.factory;

import com.dev.org.domain.AudienceType;
import com.dev.org.strategy.NotificationSaveStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSaveStrategyFactory {

    private final List<NotificationSaveStrategy> saveStrategies;

    public NotificationSaveStrategy getStrategy(AudienceType audienceType) {
        return saveStrategies.stream()
                .filter(strategy -> strategy.supports(audienceType))
                .findFirst()
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "No save strategy found for audience type: "
                                                + audienceType));
    }
}
