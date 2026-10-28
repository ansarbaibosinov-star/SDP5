package com.delivery.planner;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.strategy.RouteStrategy;

import java.util.List;
import java.util.Objects;

public class RoutePlanner {

    private RouteStrategy strategy;

    public RoutePlanner(RouteStrategy strategy) {
        setStrategy(strategy);
    }

    public void setStrategy(RouteStrategy strategy) {
        this.strategy = Objects.requireNonNull(
                strategy,
                "Strategy cannot be null"
        );
    }

    public RouteStrategy getStrategy() {
        return strategy;
    }

    public RouteDecision plan(
            List<Route> routes,
            DeliveryRequest request
    ) {
        return strategy.selectRoute(routes, request);
    }
}
