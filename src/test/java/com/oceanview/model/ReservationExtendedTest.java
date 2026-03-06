package com.oceanview.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** Extended Reservation tests — all getters/setters, edge cases. */
class ReservationExtendedTest {
    @Test @DisplayName("TC-RE001: All setters and getters")
    void allSettersGetters() {
        Reservation r = new Reservation();
        r.setReservationId(1);
        r.setReservationNumber("RES-100001");
        r.setGuestName("John Smith");
        r.setGuestAddress("123 Beach Rd");
        r.setContactNumber("+94771234567");
        r.setGuestEmail("john@test.com");
        r.setRoomId(5);
        r.setRoomType("DELUXE");
        r.setCheckInDate(LocalDate.of(2025, 8, 1));
        r.setCheckOutDate(LocalDate.of(2025, 8, 4));
        r.setStatus("CHECKED_IN");
        r.setSpecialRequests("Ocean view please");
        r.setCreatedBy(2);
        LocalDateTime now = LocalDateTime.now();
        r.setCreatedAt(now);

        assertEquals(1, r.getReservationId());
        assertEquals("RES-100001", r.getReservationNumber());
        assertEquals("John Smith", r.getGuestName());
        assertEquals("123 Beach Rd", r.getGuestAddress());
        assertEquals("+94771234567", r.getContactNumber());
        assertEquals("john@test.com", r.getGuestEmail());
        assertEquals(5, r.getRoomId());
        assertEquals("DELUXE", r.getRoomType());
        assertEquals(LocalDate.of(2025, 8, 1), r.getCheckInDate());
        assertEquals(LocalDate.of(2025, 8, 4), r.getCheckOutDate());
        assertEquals("CHECKED_IN", r.getStatus());
        assertEquals("Ocean view please", r.getSpecialRequests());
        assertEquals(2, r.getCreatedBy());
        assertEquals(now, r.getCreatedAt());
    }

    @Test @DisplayName("TC-RE002: Long stay calculation")
    void longStay() {
        Reservation r = new Reservation();
        r.setCheckInDate(LocalDate.of(2025, 1, 1));
        r.setCheckOutDate(LocalDate.of(2025, 2, 1));
        assertEquals(31, r.getNumberOfNights());
    }

    @Test @DisplayName("TC-RE003: Reservation toString")
    void reservationToString() {
        Reservation r = new Reservation();
        r.setReservationNumber("RES-100001");
        r.setGuestName("Test");
        r.setCheckInDate(LocalDate.of(2025, 8, 1));
        r.setCheckOutDate(LocalDate.of(2025, 8, 4));
        String str = r.toString();
        assertTrue(str.contains("RES-100001"));
        assertTrue(str.contains("Test"));
    }

    @Test @DisplayName("TC-RE004: Only checkIn null returns 0")
    void onlyCheckInNull() {
        Reservation r = new Reservation();
        r.setCheckOutDate(LocalDate.of(2025, 8, 4));
        assertEquals(0, r.getNumberOfNights());
    }

    @Test @DisplayName("TC-RE005: Only checkOut null returns 0")
    void onlyCheckOutNull() {
        Reservation r = new Reservation();
        r.setCheckInDate(LocalDate.of(2025, 8, 1));
        assertEquals(0, r.getNumberOfNights());
    }
}
