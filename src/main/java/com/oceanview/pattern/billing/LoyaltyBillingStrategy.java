package com.oceanview.pattern.billing;

/** Loyalty billing — 10% discount for returning guests. */
public class LoyaltyBillingStrategy implements BillingStrategy {
    private static final double DISCOUNT = 0.90;
    @Override
    public double calculateTotal(int nights, double rate) { return nights * rate * DISCOUNT; }
    @Override
    public String getStrategyName() { return "LOYALTY"; }
}
