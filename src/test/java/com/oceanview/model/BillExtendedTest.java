package com.oceanview.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/** Extended Bill tests — edge cases, all getters/setters. */
class BillExtendedTest {
    @Test @DisplayName("TC-BE001: Zero nights bill")
    void zeroNights() {
        Bill b = new Bill();
        b.setNumberOfNights(0);
        b.setRoomRate(5000);
        b.setTaxRate(10);
        b.setDiscountPercent(0);
        b.calculateTotal();
        assertEquals(0, b.getSubtotal(), 0.01);
        assertEquals(0, b.getTotalAmount(), 0.01);
    }

    @Test @DisplayName("TC-BE002: High value bill")
    void highValue() {
        Bill b = new Bill();
        b.setNumberOfNights(30);
        b.setRoomRate(20000);
        b.setTaxRate(10);
        b.setDiscountPercent(5);
        b.calculateTotal();
        assertEquals(600000, b.getSubtotal(), 0.01);
        assertEquals(30000, b.getDiscountAmount(), 0.01);
    }

    @Test @DisplayName("TC-BE003: All setters and getters")
    void allSettersGetters() {
        Bill b = new Bill();
        b.setBillId(1);
        b.setReservationId(10);
        b.setNumberOfNights(3);
        b.setRoomRate(5000);
        b.setSubtotal(15000);
        b.setTaxRate(10);
        b.setTaxAmount(1500);
        b.setDiscountPercent(5);
        b.setDiscountAmount(750);
        b.setTotalAmount(15750);
        b.setBillingStrategy("STANDARD");
        LocalDateTime now = LocalDateTime.now();
        b.setGeneratedAt(now);
        b.setGeneratedBy(1);

        assertEquals(1, b.getBillId());
        assertEquals(10, b.getReservationId());
        assertEquals(3, b.getNumberOfNights());
        assertEquals(5000, b.getRoomRate());
        assertEquals(15000, b.getSubtotal());
        assertEquals(10, b.getTaxRate());
        assertEquals(1500, b.getTaxAmount());
        assertEquals(5, b.getDiscountPercent());
        assertEquals(750, b.getDiscountAmount());
        assertEquals(15750, b.getTotalAmount());
        assertEquals("STANDARD", b.getBillingStrategy());
        assertEquals(now, b.getGeneratedAt());
        assertEquals(1, b.getGeneratedBy());
    }

    @Test @DisplayName("TC-BE004: Default tax rate is 10")
    void defaultTaxRate() {
        Bill b = new Bill();
        assertEquals(10.0, b.getTaxRate());
        assertEquals(0.0, b.getDiscountPercent());
    }

    @Test @DisplayName("TC-BE005: Bill toString")
    void billToString() {
        Bill b = new Bill();
        b.setBillId(1);
        b.setReservationId(10);
        b.setTotalAmount(16500);
        String str = b.toString();
        assertTrue(str.contains("1"));
        assertTrue(str.contains("10"));
    }

    @Test @DisplayName("TC-BE006: 100% discount")
    void fullDiscount() {
        Bill b = new Bill();
        b.setNumberOfNights(2);
        b.setRoomRate(10000);
        b.setTaxRate(10);
        b.setDiscountPercent(100);
        b.calculateTotal();
        assertEquals(20000, b.getDiscountAmount(), 0.01);
        assertEquals(0, b.getTotalAmount(), 0.01);
    }
}
