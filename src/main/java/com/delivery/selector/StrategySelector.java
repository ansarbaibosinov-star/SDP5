package com.delivery.selector;

import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.strategy.RouteStrategy;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class StrategySelector {

    private final Map<DeliveryPriority, RouteStrategy> strategies =
            new EnumMap<>(DeliveryPriority.class);

    public StrategySelector(
            Map<DeliveryPriority, RouteStrategy> initialStrategies
    ) {
        Objects.requireNonNull(
                initialStrategies,
                "Strategies map cannot be null"
        );

        initialStrategies.forEach(this::registerStrategy);
    }

    public void registerStrategy(
            DeliveryPriority priority,
            RouteStrategy strategy
    ) {
        Objects.requireNonNull(priority, "Priority cannot be null");
        Objects.requireNonNull(strategy, "Strategy cannot be null");

        strategies.put(priority, strategy);
    }


    public RouteStrategy selectStrategy(DeliveryRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");

        if (request.ecoPreferred()) {
            RouteStrategy ecoStrategy = strategies.get(DeliveryPriority.ECO);

            if (ecoStrategy != null) {
                return ecoStrategy;
            }
        }

        RouteStrategy strategy = strategies.get(request.priority());

        if (strategy == null) {
            throw new IllegalStateException(
                    "No strategy registered for priority: "
                            + request.priority()
            );
        }

        return strategy;
    }

}
