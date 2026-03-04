package com.oceanview.pattern.billing;

// This is the Strategy pattern - it lets us swap different billing calculations at runtime
// An interface is like a contract - any class that implements this MUST provide these two methods
// This way we can have Standard billing, Seasonal billing, Loyalty billing, etc.
// and switch between them without changing the rest of our code
public interface BillingStrategy {

    // Every billing strategy must be able to calculate a total given nights and rate per night
    double calculateTotal(int nights, double ratePerNight);

    // Every billing strategy must have a name so we can identify which one is being used
    String getStrategyName();
}
