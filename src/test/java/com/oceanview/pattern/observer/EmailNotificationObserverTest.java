package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * EmailNotificationObserverTest — Tests the Observer pattern email notification.
 * Verifies both created and cancelled notification methods execute without error.
 */
class EmailNotificationObserverTest {

    @Test @DisplayName("TC-EN001: onReservationCreated prints email notification")
    void onReservationCreated() {
        EmailNotificationObserver observer = new EmailNotificationObserver();
        Reservation r = new Reservation();
        r.setReservationNumber("RES-100001");
        r.setGuestEmail("test@test.com");

        // Should not throw any exception
        assertDoesNotThrow(() -> observer.onReservationCreated(r));
    }

    @Test @DisplayName("TC-EN002: onReservationCancelled prints email notification")
    void onReservationCancelled() {
        EmailNotificationObserver observer = new EmailNotificationObserver();
        Reservation r = new Reservation();
        r.setReservationNumber("RES-100002");
        r.setGuestEmail("cancel@test.com");

        assertDoesNotThrow(() -> observer.onReservationCancelled(r));
    }

    @Test @DisplayName("TC-EN003: Observer implements ReservationObserver")
    void implementsInterface() {
        EmailNotificationObserver observer = new EmailNotificationObserver();
        assertTrue(observer instanceof ReservationObserver);
    }
}
