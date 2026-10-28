
package com.delivery.model;

import java.util.List;

public record RouteDecision(
        Route route,
        String strategyName,
        double score,
        String explanation,
        List<String> rejectedRoutes
) {
    public RouteDecision {
        if (route == null) {
            throw new IllegalArgumentException(
                    "Selected route is required"
            );
        }

        if (strategyName == null || strategyName.isBlank()) {
            throw new IllegalArgumentException(
                    "Strategy name is required"
            );
        }

        if (!Double.isFinite(score)) {
            throw new IllegalArgumentException(
                    "Score must be finite"
            );
        }

        if (explanation == null || explanation.isBlank()) {
            throw new IllegalArgumentException(
                    "Explanation is required"
            );
        }

        rejectedRoutes = rejectedRoutes == null
                ? List.of()
                : List.copyOf(rejectedRoutes);
    }
}
