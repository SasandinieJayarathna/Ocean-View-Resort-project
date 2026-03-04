package com.oceanview.pattern.billing;

// This is the loyalty billing strategy - it gives a 10% discount to returning/loyal guests
// It implements BillingStrategy so it follows the same contract as the other strategies
public class LoyaltyBillingStrategy implements BillingStrategy {

    // Multiplying by 0.90 is the same as giving a 10% discount
    // For example: $100 * 0.90 = $90 (the guest saves $10)
    private static final double DISCOUNT = 0.90;

    // Calculate the total with the loyalty discount applied
    // For example: 3 nights * $100 rate * 0.90 = $270 (instead of $300 without discount)
    @Override
    public double calculateTotal(int nights, double rate) { return nights * rate * DISCOUNT; }

    // Returns the name of this strategy
    @Override
    public String getStrategyName() { return "LOYALTY"; }
}
