package com.delivery.strategy;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.service.RouteEligibilityService;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public abstract class AbstractRouteStrategy implements RouteStrategy {

    protected final RouteEligibilityService eligibilityService;

    protected AbstractRouteStrategy(
            RouteEligibilityService eligibilityService
    ) {
        this.eligibilityService = Objects.requireNonNull(
                eligibilityService,
                "Eligibility service cannot be null"
        );
    }

    protected abstract double calculateScore(
            Route route,
            DeliveryRequest request,
            List<Route> eligibleRoutes
    );

    protected abstract String explain(
            Route route,
            DeliveryRequest request,
            double score
    );

    @Override
    public final RouteDecision selectRoute(
            List<Route> routes,
            DeliveryRequest request
    ) {
        List<Route> eligible =
                eligibilityService.getEligibleRoutes(routes, request);

        List<String> rejected =
                eligibilityService.getRejectedRoutes(routes, request);

        Route selected = eligible.stream()
                .min(Comparator
                        .comparingDouble((Route route) ->
                                calculateScore(route, request, eligible))
                        .thenComparing(Route::id))
                .orElseThrow(() -> new NoSuitableRouteException(
                        "No suitable route found"
                ));

        double score = calculateScore(selected, request, eligible);

        return new RouteDecision(
                selected,
                getStrategyName(),
                score,
                explain(selected, request, score),
                rejected
        );
    }

    protected double normalize(
            double value,
            double min,
            double max
    ) {
        if (max == min) {
            return 0.0;
        }

        return (value - min) / (max - min);
    }

    protected double minValue(
            List<Route> routes,
            java.util.function.ToDoubleFunction<Route> extractor
    ) {
        return routes.stream()
                .mapToDouble(extractor)
                .min()
                .orElseThrow();
    }

    protected double maxValue(
            List<Route> routes,
            java.util.function.ToDoubleFunction<Route> extractor
    ) {
        return routes.stream()
                .mapToDouble(extractor)
                .max()
                .orElseThrow();
    }
}
