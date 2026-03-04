package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;

// This observer sends email notifications when reservations are created or cancelled
// It implements ReservationObserver so it can be registered with the ReservationNotifier
// Right now it just prints to the console (a "stub") - in a real app it would send actual emails
public class EmailNotificationObserver implements ReservationObserver {

    // When a reservation is created, we "send" a confirmation email to the guest
    // We get the guest's email and reservation number from the Reservation object
    @Override
    public void onReservationCreated(Reservation r) {
        System.out.println("[EMAIL] Confirmation email sent to " + r.getGuestEmail()
            + " for reservation " + r.getReservationNumber());
    }

    // When a reservation is cancelled, we "send" a cancellation email to the guest
    @Override
    public void onReservationCancelled(Reservation r) {
        System.out.println("[EMAIL] Cancellation email sent to " + r.getGuestEmail()
            + " for reservation " + r.getReservationNumber());
    }
}
