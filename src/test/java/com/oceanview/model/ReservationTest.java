package com.oceanview.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

/** TDD tests for Reservation model — written BEFORE DAO implementation. */
class ReservationTest {
    @Test @DisplayName("TC-M001: 3-night stay calculates correctly")
    void threeNights() {
        Reservation r = new Reservation();
        r.setCheckInDate(LocalDate.of(2025, 6, 1));
        r.setCheckOutDate(LocalDate.of(2025, 6, 4));
        assertEquals(3, r.getNumberOfNights());
    }

    @Test @DisplayName("TC-M002: 1-night stay calculates correctly")
    void oneNight() {
        Reservation r = new Reservation();
        r.setCheckInDate(LocalDate.of(2025, 7, 10));
        r.setCheckOutDate(LocalDate.of(2025, 7, 11));
        assertEquals(1, r.getNumberOfNights());
    }

    @Test @DisplayName("TC-M003: Null dates return 0 nights")
    void nullDates() { assertEquals(0, new Reservation().getNumberOfNights()); }

    @Test @DisplayName("TC-M004: Default status is CONFIRMED")
    void defaultStatus() { assertEquals("CONFIRMED", new Reservation().getStatus()); }

    @Test @DisplayName("TC-M005: Reservation number format RES-XXXXXX")
    void resFormat() {
        Reservation r = new Reservation();
        r.setReservationNumber("RES-100001");
        assertTrue(r.getReservationNumber().matches("RES-\\d{6}"));
    }
}
