package com.oceanview.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/** TDD tests for Bill calculation logic. */
class BillTest {
    @Test @DisplayName("TC-B001: Standard bill — 3 nights × 5000, 10% tax, no discount")
    void standardBill() {
        Bill b = new Bill(); b.setNumberOfNights(3); b.setRoomRate(5000); b.setTaxRate(10); b.setDiscountPercent(0); b.calculateTotal();
        assertEquals(15000, b.getSubtotal(), 0.01);
        assertEquals(1500, b.getTaxAmount(), 0.01);
        assertEquals(16500, b.getTotalAmount(), 0.01);
    }

    @Test @DisplayName("TC-B002: Bill with 10% discount applied before tax")
    void discountBill() {
        Bill b = new Bill(); b.setNumberOfNights(2); b.setRoomRate(10000); b.setTaxRate(10); b.setDiscountPercent(10); b.calculateTotal();
        assertEquals(2000, b.getDiscountAmount(), 0.01);
        assertEquals(1800, b.getTaxAmount(), 0.01);
        assertEquals(19800, b.getTotalAmount(), 0.01);
    }

    @Test @DisplayName("TC-B003: Suite 1-night bill")
    void suiteBill() {
        Bill b = new Bill(); b.setNumberOfNights(1); b.setRoomRate(20000); b.setTaxRate(10); b.setDiscountPercent(0); b.calculateTotal();
        assertEquals(22000, b.getTotalAmount(), 0.01);
    }
}
