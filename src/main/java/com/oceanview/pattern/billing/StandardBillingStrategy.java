package com.oceanview.pattern.billing;

/** Standard billing — no surcharge, no discount. */
public class StandardBillingStrategy implements BillingStrategy {
    @Override
    public double calculateTotal(int nights, double rate) { return nights * rate; }
    @Override
    public String getStrategyName() { return "STANDARD"; }
}
