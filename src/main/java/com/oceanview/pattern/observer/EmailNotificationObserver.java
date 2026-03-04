package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;

/**
 * EmailNotificationObserver — Sends email notifications (stubbed).
 * In production, this would use JavaMail to send real emails.
 */
public class EmailNotificationObserver implements ReservationObserver {
    @Override
    public void onReservationCreated(Reservation r) {
        System.out.println("[EMAIL] Confirmation email sent to " + r.getGuestEmail()
            + " for reservation " + r.getReservationNumber());
    }

    @Override
    public void onReservationCancelled(Reservation r) {
        System.out.println("[EMAIL] Cancellation email sent to " + r.getGuestEmail()
            + " for reservation " + r.getReservationNumber());
    }
}
