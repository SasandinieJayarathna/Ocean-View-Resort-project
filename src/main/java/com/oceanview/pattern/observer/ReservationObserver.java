package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;

/**
 * ReservationObserver — Observer pattern interface.
 * PATTERN: Observer (Behavioral) — decouples reservation logic from notification.
 */
public interface ReservationObserver {
    void onReservationCreated(Reservation reservation);
    void onReservationCancelled(Reservation reservation);
}
