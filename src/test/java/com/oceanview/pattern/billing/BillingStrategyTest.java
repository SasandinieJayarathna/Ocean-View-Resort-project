package com.oceanview.pattern.billing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class BillingStrategyTest {
    @Test @DisplayName("TC-S001: Standard = nights x rate")
    void standard() { assertEquals(15000, new StandardBillingStrategy().calculateTotal(3, 5000), 0.01); }

    @Test @DisplayName("TC-S002: Seasonal = nights x rate x 1.20")
    void seasonal() { assertEquals(18000, new SeasonalBillingStrategy().calculateTotal(3, 5000), 0.01); }

    @Test @DisplayName("TC-S003: Loyalty = nights x rate x 0.90")
    void loyalty() { assertEquals(13500, new LoyaltyBillingStrategy().calculateTotal(3, 5000), 0.01); }

    @Test @DisplayName("TC-S004: Strategy names correct")
    void names() {
        assertEquals("STANDARD", new StandardBillingStrategy().getStrategyName());
        assertEquals("SEASONAL", new SeasonalBillingStrategy().getStrategyName());
        assertEquals("LOYALTY", new LoyaltyBillingStrategy().getStrategyName());
    }
}
