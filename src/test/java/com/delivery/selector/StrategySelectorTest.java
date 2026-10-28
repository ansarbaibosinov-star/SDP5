package com.delivery.selector;

import com.delivery.model.DeliveryPriority;
import com.delivery.model.DeliveryRequest;
import com.delivery.service.RouteEligibilityService;
import com.delivery.strategy.BalancedRouteStrategy;
import com.delivery.strategy.CheapestRouteStrategy;
import com.delivery.strategy.EcoFriendlyRouteStrategy;
import com.delivery.strategy.FastestRouteStrategy;
import com.delivery.strategy.RouteStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StrategySelectorTest {

    private StrategySelector selector;
    private RouteStrategy fastest;
    private RouteStrategy cheapest;
    private RouteStrategy eco;
    private RouteStrategy balanced;

    @BeforeEach
    void setUp() {
        RouteEligibilityService eligibility = new RouteEligibilityService();

        fastest = new FastestRouteStrategy(eligibility);
        cheapest = new CheapestRouteStrategy(eligibility);
        eco = new EcoFriendlyRouteStrategy(eligibility);
        balanced = new BalancedRouteStrategy(
                eligibility, 0.4, 0.3, 0.1, 0.2
        );

        Map<DeliveryPriority, RouteStrategy> strategies =
                new EnumMap<>(DeliveryPriority.class);

        strategies.put(DeliveryPriority.URGENT, fastest);
        strategies.put(DeliveryPriority.ECONOMY, cheapest);
        strategies.put(DeliveryPriority.ECO, eco);
        strategies.put(DeliveryPriority.STANDARD, balanced);

        selector = new StrategySelector(strategies);
    }

    private DeliveryRequest request(
            DeliveryPriority priority, boolean ecoPreferred) {
        return new DeliveryRequest(
                priority, 10000, 200,
                ecoPreferred, 20, Set.of()
        );
    }

    @Test
    void urgentRequestSelectsFastest() {
        assertSame(fastest,
                selector.selectStrategy(request(DeliveryPriority.URGENT, false)));
    }

    @Test
    void economyRequestSelectsCheapest() {
        assertSame(cheapest,
                selector.selectStrategy(request(DeliveryPriority.ECONOMY, false)));
    }

    @Test
    void ecoPrioritySelectsEcoStrategy() {
        assertSame(eco,
                selector.selectStrategy(request(DeliveryPriority.ECO, false)));
    }

    @Test
    void standardRequestSelectsBalanced() {
        assertSame(balanced,
                selector.selectStrategy(request(DeliveryPriority.STANDARD, false)));
    }

    @Test
    void ecoPreferenceOverridesNormalPriority() {
        assertSame(eco,
                selector.selectStrategy(request(DeliveryPriority.STANDARD, true)));
    }
}
