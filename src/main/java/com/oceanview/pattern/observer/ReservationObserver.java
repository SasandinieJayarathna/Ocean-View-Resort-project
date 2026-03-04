package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;

// This is the Observer pattern - it lets us notify multiple objects when something happens
// This interface defines what an "observer" must be able to do
// Any class that wants to be notified about reservation events must implement this interface
// For example, an email sender and a logger can both implement this to react to reservations
public interface ReservationObserver {

    // This method gets called whenever a new reservation is created
    void onReservationCreated(Reservation reservation);

    // This method gets called whenever a reservation is cancelled
    void onReservationCancelled(Reservation reservation);
}
