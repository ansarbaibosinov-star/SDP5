
package com.delivery.demo;

import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Location;
import com.delivery.model.Route;
import com.delivery.model.RouteDecision;
import com.delivery.planner.RoutePlanner;
import com.delivery.selector.StrategySelector;
import com.delivery.service.RouteComparisonService;
import com.delivery.service.RouteEligibilityService;
import com.delivery.strategy.BalancedRouteStrategy;
import com.delivery.strategy.CheapestRouteStrategy;
import com.delivery.strategy.DistanceFirstRouteStrategy;
import com.delivery.strategy.EcoFriendlyRouteStrategy;
import com.delivery.strategy.FastestRouteStrategy;
import com.delivery.strategy.RouteStrategy;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        RouteEligibilityService eligibility =
                new RouteEligibilityService();

        // 1. Create locations
        Location astana = new Location(
                "Astana", 51.1694, 71.4491
        );

        Location karaganda = new Location(
                "Karaganda", 49.8060, 73.0850
        );

        // 2. Create candidate routes
        List<Route> routes = List.of(
                new Route("R1", astana, karaganda,
                        215, 150, 2500, 8.5, 2, true),

                new Route("R2", astana, karaganda,
                        230, 115, 3200, 10.0, 1, true),

                new Route("R3", astana, karaganda,
                        205, 170, 1800, 7.0, 5, true),

                new Route("R4", astana, karaganda,
                        240, 140, 2200, 5.0, 2, true),

                new Route("R5", astana, karaganda,
                        190, 180, 2800, 6.5, 3, true),

                new Route("R6", astana, karaganda,
                        225, 130, 2900, 4.0, 1, true),

                new Route("R7", astana, karaganda,
                        200, 160, 1500, 9.5, 4, false),

                new Route("R8", astana, karaganda,
                        250, 200, 1200, 3.0, 2, true)
        );

        // 3. Create all five strategies
        RouteStrategy fastest =
                new FastestRouteStrategy(eligibility);

        RouteStrategy cheapest =
                new CheapestRouteStrategy(eligibility);

        RouteStrategy eco =
                new EcoFriendlyRouteStrategy(eligibility);

        RouteStrategy balanced =
                new BalancedRouteStrategy(
                        eligibility,
                        0.40, 0.30, 0.10, 0.20
                );

        RouteStrategy shortest =
                new DistanceFirstRouteStrategy(eligibility);

        // 4. Create the planner
        RoutePlanner planner = new RoutePlanner(fastest);

        // 5. Create an automatic strategy selector
        Map<DeliveryPriority, RouteStrategy> strategyMap =
                new EnumMap<>(DeliveryPriority.class);

        strategyMap.put(DeliveryPriority.URGENT, fastest);
        strategyMap.put(DeliveryPriority.ECONOMY, cheapest);
        strategyMap.put(DeliveryPriority.ECO, eco);
        strategyMap.put(DeliveryPriority.STANDARD, balanced);

        StrategySelector selector =
                new StrategySelector(strategyMap);

        // 6. Create a delivery request
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD,
                15000,
                250,
                false,
                20.0,
                Set.of()
        );

        System.out.println("==================================");
        System.out.println(" SMART DELIVERY ROUTE PLANNER");
        System.out.println("==================================");

        System.out.println("\nCandidate routes: " + routes.size());

        // 7. Demonstrate automatic selection
        System.out.println("\n--- AUTOMATIC STRATEGY SELECTION ---");

        RouteStrategy automatic = selector.selectStrategy(request);
        planner.setStrategy(automatic);

        printDecision(planner.plan(routes, request));

        // 8. Demonstrate runtime switching
        System.out.println("\n--- RUNTIME STRATEGY SWITCHING ---");

        planner.setStrategy(fastest);
        printDecision(planner.plan(routes, request));

        planner.setStrategy(cheapest);
        printDecision(planner.plan(routes, request));

        planner.setStrategy(eco);
        printDecision(planner.plan(routes, request));

        planner.setStrategy(balanced);
        printDecision(planner.plan(routes, request));

        // 9. Demonstrate the fifth strategy
        System.out.println("\n--- FIFTH STRATEGY ---");

        planner.setStrategy(shortest);
        printDecision(planner.plan(routes, request));

        // 10. Compare all strategies
        System.out.println("\n--- STRATEGY COMPARISON ---");

        RouteComparisonService comparison =
                new RouteComparisonService(
                        planner,
                        List.of(fastest, cheapest, eco, balanced, shortest)
                );

        List<RouteDecision> decisions =
                comparison.compare(routes, request);

        for (RouteDecision decision : decisions) {
            System.out.println("\nStrategy: "
                    + decision.strategyName());

            System.out.println("Route: "
                    + decision.route().id());

            System.out.printf("Distance: %.1f km%n",
                    decision.route().distanceKm());

            System.out.println("Effective time: "
                    + eligibility.calculateEffectiveMinutes(
                    decision.route()) + " min");

            System.out.printf("Total cost: %.2f%n",
                    eligibility.calculateTotalCost(
                            decision.route(), request));

            System.out.printf("CO2: %.2f kg%n",
                    decision.route().carbonEmission());

            System.out.printf("Score: %.4f%n", decision.score());
        }

        // 11. Show rejected routes
        System.out.println("\n--- REJECTED ROUTES ---");

        planner.setStrategy(fastest);

        RouteDecision finalDecision =
                planner.plan(routes, request);

        if (finalDecision.rejectedRoutes().isEmpty()) {
            System.out.println("No routes were rejected.");
        } else {
            finalDecision.rejectedRoutes().forEach(
                    System.out::println
            );
        }

        System.out.println("\n--- PROGRAM FINISHED ---");
    }

    private static void printDecision(RouteDecision decision) {
        System.out.println("\nStrategy: " + decision.strategyName());

        System.out.println("Selected route: "
                + decision.route().id());

        System.out.println("From: "
                + decision.route().origin().name());

        System.out.println("To: "
                + decision.route().destination().name());

        System.out.printf("Distance: %.1f km%n",
                decision.route().distanceKm());

        System.out.println("Estimated time: "
                + decision.route().estimatedMinutes() + " min");

        System.out.printf("Score: %.4f%n", decision.score());

        System.out.println("Reason: " + decision.explanation());
    }
}
