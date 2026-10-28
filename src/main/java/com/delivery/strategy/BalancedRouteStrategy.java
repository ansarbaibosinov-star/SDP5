package com.delivery.strategy;

import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;
import com.delivery.service.RouteEligibilityService;

import java.util.List;

public class BalancedRouteStrategy extends AbstractRouteStrategy {

    private final double timeWeight;
    private final double costWeight;
    private final double distanceWeight;
    private final double emissionWeight;

    public BalancedRouteStrategy(
            RouteEligibilityService eligibilityService,
            double timeWeight,
            double costWeight,
            double distanceWeight,
            double emissionWeight
    ) {
        super(eligibilityService);

        double[] weights = {
                timeWeight, costWeight, distanceWeight, emissionWeight
        };

        for (double weight : weights) {
            if (!Double.isFinite(weight)
                    || weight < 0 || weight > 1) {
                throw new IllegalArgumentException(
                        "Weights must be between 0 and 1"
                );
            }
        }

        double sum = timeWeight + costWeight
                + distanceWeight + emissionWeight;

        if (Math.abs(sum - 1.0) > 1e-9) {
            throw new IllegalArgumentException(
                    "Strategy weights must sum to 1.0"
            );
        }

        this.timeWeight = timeWeight;
        this.costWeight = costWeight;
        this.distanceWeight = distanceWeight;
        this.emissionWeight = emissionWeight;
    }

    @Override
    protected double calculateScore(
            Route route,
            DeliveryRequest request,
            List<Route> eligibleRoutes
    ) {
        double time = eligibilityService.calculateEffectiveMinutes(route);
        double cost = eligibilityService.calculateTotalCost(route, request);
        double distance = route.distanceKm();
        double emission = route.carbonEmission();

        double normalizedTime = normalize(
                time,
                minValue(eligibleRoutes, r ->
                        eligibilityService.calculateEffectiveMinutes(r)),
                maxValue(eligibleRoutes, r ->
                        eligibilityService.calculateEffectiveMinutes(r))
        );

        double normalizedCost = normalize(
                cost,
                minValue(eligibleRoutes, r ->
                        eligibilityService.calculateTotalCost(r, request)),
                maxValue(eligibleRoutes, r ->
                        eligibilityService.calculateTotalCost(r, request))
        );

        double normalizedDistance = normalize(
                distance,
                minValue(eligibleRoutes, Route::distanceKm),
                maxValue(eligibleRoutes, Route::distanceKm)
        );

        double normalizedEmission = normalize(
                emission,
                minValue(eligibleRoutes, Route::carbonEmission),
                maxValue(eligibleRoutes, Route::carbonEmission)
        );

        return normalizedTime * timeWeight
                + normalizedCost * costWeight
                + normalizedDistance * distanceWeight
                + normalizedEmission * emissionWeight;
    }

    @Override
    protected String explain(
            Route route,
            DeliveryRequest request,
            double score
    ) {
        return String.format(
                "Balanced normalized score: %.4f. "
                        + "Weights: time=%.2f, cost=%.2f, "
                        + "distance=%.2f, emissions=%.2f.",
                score,
                timeWeight,
                costWeight,
                distanceWeight,
                emissionWeight
        );
    }

    @Override
    public String getStrategyName() {
        return "Balanced";
    }
}
