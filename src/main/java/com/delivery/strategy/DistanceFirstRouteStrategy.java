package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.service.RouteEligibilityService;

import java.util.List;

public class DistanceFirstRouteStrategy extends AbstractRouteStrategy {

    public DistanceFirstRouteStrategy(
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
        return route.distanceKm();
    }

    @Override
    protected String explain(
            Route route,
            DeliveryRequest request,
            double score
    ) {
        return String.format(
                "Selected for minimum distance: %.2f km.",
                route.distanceKm()
        );
    }

    @Override
    public String getStrategyName() {
        return "Distance-first";
    }
}
