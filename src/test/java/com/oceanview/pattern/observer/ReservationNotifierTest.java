package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class ReservationNotifierTest {
    private Reservation testReservation() {
        Reservation r = new Reservation();
        r.setReservationNumber("RES-100001");
        r.setGuestName("Test Guest");
        r.setGuestEmail("test@test.com");
        return r;
    }

    @Test @DisplayName("TC-O001: Observer count correct")
    void observerCount() {
        ReservationNotifier n = new ReservationNotifier();
        assertEquals(0, n.getObserverCount());
        n.addObserver(new LoggingObserver());
        assertEquals(1, n.getObserverCount());
        n.addObserver(new EmailNotificationObserver());
        assertEquals(2, n.getObserverCount());
    }

    @Test @DisplayName("TC-O002: Notify created does not throw")
    void notifyCreated() {
        ReservationNotifier n = new ReservationNotifier();
        n.addObserver(new LoggingObserver());
        n.addObserver(new EmailNotificationObserver());
        assertDoesNotThrow(() -> n.notifyCreated(testReservation()));
    }

    @Test @DisplayName("TC-O003: Notify cancelled does not throw")
    void notifyCancelled() {
        ReservationNotifier n = new ReservationNotifier();
        n.addObserver(new LoggingObserver());
        assertDoesNotThrow(() -> n.notifyCancelled(testReservation()));
    }

    @Test @DisplayName("TC-O004: Remove observer works")
    void removeObserver() {
        ReservationNotifier n = new ReservationNotifier();
        LoggingObserver lo = new LoggingObserver();
        n.addObserver(lo);
        assertEquals(1, n.getObserverCount());
        n.removeObserver(lo);
        assertEquals(0, n.getObserverCount());
    }
}
