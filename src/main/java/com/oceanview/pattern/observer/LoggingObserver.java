package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;
import java.time.LocalDateTime;

/** LoggingObserver — Logs reservation events with timestamps. */
public class LoggingObserver implements ReservationObserver {
    @Override
    public void onReservationCreated(Reservation r) {
        System.out.println("[LOG " + LocalDateTime.now() + "] CREATED: " + r.getReservationNumber() + " for " + r.getGuestName());
    }

    @Override
    public void onReservationCancelled(Reservation r) {
        System.out.println("[LOG " + LocalDateTime.now() + "] CANCELLED: " + r.getReservationNumber());
    }
}
