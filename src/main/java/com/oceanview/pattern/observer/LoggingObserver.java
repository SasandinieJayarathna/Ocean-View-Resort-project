package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;
import java.time.LocalDateTime;

// This observer logs reservation events with timestamps for record-keeping
// It implements ReservationObserver so it can be registered with the ReservationNotifier
// This is useful for debugging and keeping an audit trail of what happened and when
public class LoggingObserver implements ReservationObserver {

    // When a reservation is created, log the time, reservation number, and guest name
    // LocalDateTime.now() gives us the current date and time for the log entry
    @Override
    public void onReservationCreated(Reservation r) {
        System.out.println("[LOG " + LocalDateTime.now() + "] CREATED: " + r.getReservationNumber() + " for " + r.getGuestName());
    }

    // When a reservation is cancelled, log the time and reservation number
    @Override
    public void onReservationCancelled(Reservation r) {
        System.out.println("[LOG " + LocalDateTime.now() + "] CANCELLED: " + r.getReservationNumber());
    }
}
