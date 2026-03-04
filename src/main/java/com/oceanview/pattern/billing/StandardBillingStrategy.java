package com.oceanview.pattern.billing;

// This is the standard (normal) billing strategy - no extra charges and no discounts
// It implements BillingStrategy, which means it must provide the two methods from that interface
public class StandardBillingStrategy implements BillingStrategy {

    // @Override means we are providing our own version of a method from the interface
    // Standard billing is simple: just multiply the number of nights by the rate per night
    @Override
    public double calculateTotal(int nights, double rate) { return nights * rate; }

    // Returns the name of this strategy so we know which billing type was used
    @Override
    public String getStrategyName() { return "STANDARD"; }
}
