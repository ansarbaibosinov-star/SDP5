
package com.delivery.service;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.planner.RoutePlanner;
import com.delivery.strategy.RouteStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouteComparisonService {

    private final RoutePlanner planner;
    private final List<RouteStrategy> strategies;

    public RouteComparisonService(
            RoutePlanner planner,
            List<RouteStrategy> strategies
    ) {
        this.planner = Objects.requireNonNull(
                planner,
                "Planner cannot be null"
        );

        if (strategies == null || strategies.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one strategy is required"
            );
        }

        this.strategies = List.copyOf(strategies);
    }

    public List<RouteDecision> compare(
            List<Route> routes,
            DeliveryRequest request
    ) {
        List<RouteDecision> results = new ArrayList<>();

        for (RouteStrategy strategy : strategies) {
            planner.setStrategy(strategy);
            results.add(planner.plan(routes, request));
        }

        return List.copyOf(results);
    }
}
