package com.delivery.service;

import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RouteEligibilityService {

    public double calculateTotalCost(
            Route route,
            DeliveryRequest request
    ) {
        double distanceCost = route.distanceKm() * 35.0;

        double prioritySurcharge =
                request.priority() == DeliveryPriority.URGENT
                        ? 1500.0
                        : 0.0;

        double trafficFee = route.trafficLevel() * 50.0;

        return route.cost()
                + distanceCost
                + prioritySurcharge
                + trafficFee;
    }

    public int calculateEffectiveMinutes(Route route) {
        return route.estimatedMinutes()
                + route.trafficLevel() * 2;
    }

    public String rejectionReason(
            Route route,
            DeliveryRequest request
    ) {
        Objects.requireNonNull(route, "Route cannot be null");
        Objects.requireNonNull(request, "Request cannot be null");

        if (!route.available()) {
            return "Route is unavailable";
        }

        if (request.prohibitedRouteIds().contains(route.id())) {
            return "Route is prohibited for this delivery";
        }

        if (calculateTotalCost(route, request) > request.maxBudget()) {
            return "Budget exceeded";
        }

        if (calculateEffectiveMinutes(route)
                > request.maxDeliveryMinutes()) {
            return "Delivery deadline exceeded";
        }

        if (route.carbonEmission() > request.maxCarbonEmission()) {
            return "Carbon emission limit exceeded";
        }

        return null;
    }

    public List<Route> getEligibleRoutes(
            List<Route> routes,
            DeliveryRequest request
    ) {
        validateInput(routes, request);

        List<Route> eligible = new ArrayList<>();

        for (Route route : routes) {
            if (route == null) {
                throw new IllegalArgumentException(
                        "Route list cannot contain null"
                );
            }

            if (rejectionReason(route, request) == null) {
                eligible.add(route);
            }
        }

        if (eligible.isEmpty()) {
            throw new com.delivery.exception.NoSuitableRouteException(
                    "No suitable route for request: " + request.priority()
            );
        }

        return List.copyOf(eligible);
    }

    public List<String> getRejectedRoutes(
            List<Route> routes,
            DeliveryRequest request
    ) {
        validateInput(routes, request);

        List<String> rejected = new ArrayList<>();

        for (Route route : routes) {
            if (route == null) {
                throw new IllegalArgumentException(
                        "Route list cannot contain null"
                );
            }

            String reason = rejectionReason(route, request);

            if (reason != null) {
                rejected.add(route.id() + ": " + reason);
            }
        }

        return List.copyOf(rejected);
    }

    private void validateInput(
            List<Route> routes,
            DeliveryRequest request
    ) {
        Objects.requireNonNull(request, "Request cannot be null");

        if (routes == null || routes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Route list cannot be null or empty"
            );
        }
    }
}
