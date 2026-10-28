package com.delivery.service;

import com.delivery.exception.NoSuitableRouteException;
import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.model.Location;
import com.delivery.model.Route;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouteEligibilityServiceTest {

    private RouteEligibilityService service;
    private Location from;
    private Location to;
    private Route route;

    @BeforeEach
    void setUp() {
        service = new RouteEligibilityService();
        from = new Location("Astana", 51.0, 71.0);
        to = new Location("Karaganda", 49.0, 73.0);
        route = new Route("R1", from, to, 100, 60, 1000, 5, 2, true);
    }

    @Test
    void calculatesCostIncludingDistanceAndTraffic() {
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 10000, 200,
                false, 20, Set.of()
        );

        // 1000 + (100 * 35) + (2 * 50) = 4600
        assertEquals(4600.0,
                service.calculateTotalCost(route, request), 0.001);
    }

    @Test
    void calculatesEffectiveTimeIncludingTraffic() {
        assertEquals(64, service.calculateEffectiveMinutes(route));
    }

    @Test
    void rejectsUnavailableRoute() {
        Route unavailable = new Route(
                "R2", from, to, 100, 60, 1000, 5, 2, false
        );

        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 10000, 200,
                false, 20, Set.of()
        );

        assertNotNull(service.rejectionReason(unavailable, request));
    }

    @Test
    void rejectsProhibitedRoute() {
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 10000, 200,
                false, 20, Set.of("R1")
        );

        assertNotNull(service.rejectionReason(route, request));
    }

    @Test
    void rejectsRouteOverBudget() {
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 1000, 200,
                false, 20, Set.of()
        );

        assertNotNull(service.rejectionReason(route, request));
    }

    @Test
    void rejectsRouteOverDeadline() {
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 10000, 50,
                false, 20, Set.of()
        );

        assertNotNull(service.rejectionReason(route, request));
    }

    @Test
    void rejectsRouteOverEmissionLimit() {
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 10000, 200,
                false, 4, Set.of()
        );

        assertNotNull(service.rejectionReason(route, request));
    }

    @Test
    void throwsWhenNoRouteIsEligible() {
        DeliveryRequest request = new DeliveryRequest(
                DeliveryPriority.STANDARD, 1, 1,
                false, 1, Set.of()
        );

        assertThrows(NoSuitableRouteException.class,
                () -> service.getEligibleRoutes(List.of(route), request));
    }
}

