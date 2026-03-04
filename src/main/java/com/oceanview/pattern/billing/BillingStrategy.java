package com.oceanview.pattern.billing;

/**
 * BillingStrategy — Strategy pattern interface for billing calculations.
 * PATTERN: Strategy (Behavioral) — encapsulates interchangeable algorithms.
 * SOLID: Open-Closed — add new pricing without modifying BillingService.
 */
public interface BillingStrategy {
    double calculateTotal(int nights, double ratePerNight);
    String getStrategyName();
}
