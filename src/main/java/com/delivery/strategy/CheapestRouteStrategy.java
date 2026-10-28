package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.service.RouteEligibilityService;

import java.util.List;

public class CheapestRouteStrategy extends AbstractRouteStrategy {

    public CheapestRouteStrategy(
            RouteEligibilityService eligibilityService
    ) {
        super(eligibilityService);
    }

    @Override
    protected double calculateScore(
            Route route,
            DeliveryRequest request,
            List<Route> eligibleRoutes
    ) {
        return eligibilityService.calculateTotalCost(route, request);
    }

    @Override
    protected String explain(
            Route route,
            DeliveryRequest request,
            double score
    ) {
        return String.format(
                "Selected for minimum total cost: %.2f. "
                        + "Base: %.2f, distance charge: %.2f, "
                        + "traffic fee: %.2f.",
                score,
                route.cost(),
                route.distanceKm() * 35.0,
                route.trafficLevel() * 50.0
        );
    }

    @Override
    public String getStrategyName() {
        return "Cheapest";
    }
}

