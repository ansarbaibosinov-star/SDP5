package com.delivery.strategy;

import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.service.RouteEligibilityService;

import java.util.List;

public class FastestRouteStrategy extends AbstractRouteStrategy {

    public FastestRouteStrategy(
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
        double effectiveTime =
                eligibilityService.calculateEffectiveMinutes(route);

        double priorityBonus =
                request.priority() == DeliveryPriority.URGENT
                        ? -5.0
                        : 0.0;

        return effectiveTime + priorityBonus;
    }

    @Override
    protected String explain(
            Route route,
            DeliveryRequest request,
            double score
    ) {
        return "Selected for minimum effective delivery time. "
                + "Estimated time: " + route.estimatedMinutes()
                + " min, traffic penalty: "
                + route.trafficLevel() * 2
                + " min, priority: " + request.priority() + ".";
    }

    @Override
    public String getStrategyName() {
        return "Fastest";
    }
}
