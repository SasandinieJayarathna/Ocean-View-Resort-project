package com.oceanview.pattern.billing;

/** Seasonal billing — 20% surcharge during peak season. */
public class SeasonalBillingStrategy implements BillingStrategy {
    private static final double SURCHARGE = 1.20;
    @Override
    public double calculateTotal(int nights, double rate) { return nights * rate * SURCHARGE; }
    @Override
    public String getStrategyName() { return "SEASONAL"; }
}
