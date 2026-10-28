package com.delivery.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ModelValidationTest {

    @Test
    void locationAcceptsValidCoordinates() {
        Location location = new Location("Astana", 51.1694, 71.4491);

        assertEquals("Astana", location.name());
    }

    @Test
    void locationRejectsBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new Location(" ", 51.0, 71.0));
    }

    @Test
    void locationRejectsInvalidLatitude() {
        assertThrows(IllegalArgumentException.class,
                () -> new Location("Test", 91.0, 71.0));
    }

    @Test
    void locationRejectsInvalidLongitude() {
        assertThrows(IllegalArgumentException.class,
                () -> new Location("Test", 51.0, 181.0));
    }

    @Test
    void routeRejectsNegativeDistance() {
        Location a = new Location("A", 51.0, 71.0);
        Location b = new Location("B", 49.0, 73.0);

        assertThrows(IllegalArgumentException.class,
                () -> new Route("R1", a, b, -10, 100, 500, 5, 2, true));
    }

    @Test
    void routeRejectsInvalidTrafficLevel() {
        Location a = new Location("A", 51.0, 71.0);
        Location b = new Location("B", 49.0, 73.0);

        assertThrows(IllegalArgumentException.class,
                () -> new Route("R1", a, b, 100, 100, 500, 5, 11, true));
    }

    @Test
    void deliveryRequestRejectsNegativeBudget() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryRequest(
                        DeliveryPriority.STANDARD,
                        -1, 200, false, 20, Set.of()
                ));
    }

    @Test
    void deliveryRequestRejectsNegativeDeadline() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeliveryRequest(
                        DeliveryPriority.STANDARD,
                        10000, -1, false, 20, Set.of()
                ));
    }
}
