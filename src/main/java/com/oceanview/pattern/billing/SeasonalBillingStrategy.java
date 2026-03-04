package com.oceanview.pattern.billing;

// This is the seasonal billing strategy - it adds a 20% surcharge during peak/busy seasons
// It implements BillingStrategy so it follows the same contract as the other strategies
public class SeasonalBillingStrategy implements BillingStrategy {

    // This constant means we multiply by 1.20, which adds 20% on top of the normal price
    // "static final" means this value is shared across all instances and cannot be changed
    private static final double SURCHARGE = 1.20;

    // Calculate the total with the seasonal surcharge applied
    // For example: 3 nights * $100 rate * 1.20 = $360 (instead of $300 without surcharge)
    @Override
    public double calculateTotal(int nights, double rate) { return nights * rate * SURCHARGE; }

    // Returns the name of this strategy
    @Override
    public String getStrategyName() { return "SEASONAL"; }
}
