package com.delivery.model;

import java.util.Set;

public record DeliveryRequest(
        DeliveryPriority priority,
        double maxBudget,
        int maxDeliveryMinutes,
        boolean ecoPreferred,
        double maxCarbonEmission,
        Set<String> prohibitedRouteIds
) {
    public DeliveryRequest {
        if (priority == null) {
            throw new IllegalArgumentException(
                    "Delivery priority is required"
            );
        }

        if (!Double.isFinite(maxBudget) || maxBudget < 0) {
            throw new IllegalArgumentException(
                    "Budget must be finite and non-negative"
            );
        }

        if (maxDeliveryMinutes < 0) {
            throw new IllegalArgumentException(
                    "Maximum delivery time cannot be negative"
            );
        }

        if (!Double.isFinite(maxCarbonEmission)
                || maxCarbonEmission < 0) {
            throw new IllegalArgumentException(
                    "Maximum carbon emission must be finite and non-negative"
            );
        }

        prohibitedRouteIds = prohibitedRouteIds == null
                ? Set.of()
                : Set.copyOf(prohibitedRouteIds);
    }
}
