package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.service.RouteEligibilityService;

import java.util.List;

public class EcoFriendlyRouteStrategy extends AbstractRouteStrategy {

    public EcoFriendlyRouteStrategy(
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
        return route.carbonEmission() * 0.8
                + route.distanceKm() * 0.2;
    }

    @Override
    protected String explain(
            Route route,
            DeliveryRequest request,
            double score
    ) {
        return String.format(
                "Eco score: %.3f = CO2 %.3f * 0.8 + "
                        + "distance %.2f * 0.2.",
                score,
                route.carbonEmission(),
                route.distanceKm()
        );
    }

    @Override
    public String getStrategyName() {
        return "Eco-friendly";
    }
}
