package com.delivery.strategy;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Location;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.planner.RoutePlanner;
import com.delivery.service.RouteEligibilityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouteStrategyTest {

    private RouteEligibilityService eligibility;
    private List<Route> routes;
    private DeliveryRequest request;

    @BeforeEach
    void setUp() {
        eligibility = new RouteEligibilityService();

        Location from = new Location("Astana", 51.1694, 71.4491);
        Location to = new Location("Karaganda", 49.8060, 73.0850);

        routes = List.of(
                new Route("R1", from, to, 100, 60, 1000, 8, 2, true),
                new Route("R2", from, to, 150, 40, 2000, 4, 1, true),
                new Route("R3", from, to, 80, 90, 500, 10, 4, true),
                new Route("R4", from, to, 120, 70, 1500, 3, 2, true)
        );

        request = new DeliveryRequest(
                DeliveryPriority.STANDARD,
                10000,
                200,
                false,
                20,
                Set.of()
        );
    }

    @Test
    void fastestStrategySelectsEligibleRoute() {
        RouteStrategy strategy = new FastestRouteStrategy(eligibility);

        RouteDecision decision = strategy.selectRoute(routes, request);

        assertNotNull(decision.route());
        assertTrue(routes.stream()
                .anyMatch(route -> route.id().equals(decision.route().id())));
        assertEquals("Fastest", decision.strategyName());
    }

    @Test
    void cheapestStrategySelectsEligibleRoute() {
        RouteStrategy strategy = new CheapestRouteStrategy(eligibility);

        RouteDecision decision = strategy.selectRoute(routes, request);

        assertNotNull(decision.route());
        assertEquals("Cheapest", decision.strategyName());
    }

    @Test
    void ecoStrategySelectsEligibleRoute() {
        RouteStrategy strategy = new EcoFriendlyRouteStrategy(eligibility);

        RouteDecision decision = strategy.selectRoute(routes, request);

        assertNotNull(decision.route());
        assertEquals("Eco-friendly", decision.strategyName());
    }

    @Test
    void balancedStrategySelectsEligibleRoute() {
        RouteStrategy strategy = new BalancedRouteStrategy(
                eligibility, 0.4, 0.3, 0.1, 0.2
        );

        RouteDecision decision = strategy.selectRoute(routes, request);

        assertNotNull(decision.route());
        assertEquals("Balanced", decision.strategyName());
    }

    @Test
    void distanceFirstStrategySelectsEligibleRoute() {
        RouteStrategy strategy = new DistanceFirstRouteStrategy(eligibility);

        RouteDecision decision = strategy.selectRoute(routes, request);

        assertNotNull(decision.route());
        assertEquals("Distance-first", decision.strategyName());
    }

    @Test
    void balancedStrategyRejectsInvalidWeights() {
        assertThrows(IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(
                        eligibility, 0.5, 0.5, 0.5, 0.5
                ));
    }

    @Test
    void balancedStrategyRejectsNegativeWeight() {
        assertThrows(IllegalArgumentException.class,
                () -> new BalancedRouteStrategy(
                        eligibility, -0.1, 0.4, 0.3, 0.4
                ));
    }

    @Test
    void plannerCanSwitchStrategiesAtRuntime() {
        RouteStrategy fastest = new FastestRouteStrategy(eligibility);
        RouteStrategy cheapest = new CheapestRouteStrategy(eligibility);

        RoutePlanner planner = new RoutePlanner(fastest);
        RouteDecision first = planner.plan(routes, request);

        planner.setStrategy(cheapest);
        RouteDecision second = planner.plan(routes, request);

        assertEquals("Fastest", first.strategyName());
        assertEquals("Cheapest", second.strategyName());
    }

    @Test
    void plannerRejectsNullStrategy() {
        RoutePlanner planner =
                new RoutePlanner(new FastestRouteStrategy(eligibility));

        assertThrows(IllegalArgumentException.class,
                () -> planner.setStrategy(null));
    }

    @Test
    void strategyThrowsWhenEveryRouteIsUnavailable() {
        Route unavailable = new Route(
                "R5",
                routes.get(0).origin(),
                routes.get(0).destination(),
                100, 60, 1000, 5, 2, false
        );

        RouteStrategy strategy = new FastestRouteStrategy(eligibility);

        assertThrows(NoSuitableRouteException.class,
                () -> strategy.selectRoute(List.of(unavailable), request));
    }
}
