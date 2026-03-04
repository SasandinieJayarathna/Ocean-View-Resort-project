package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;
import java.util.ArrayList;
import java.util.List;

// This is the "Subject" in the Observer pattern - it keeps track of all observers
// and notifies them when something happens (like a reservation being created or cancelled)
// Think of it like a newsletter - observers "subscribe" and get notified of events
public class ReservationNotifier {

    // This list holds all the observers that want to be notified
    // "final" means we cannot replace this list with a new one, but we can still add/remove items
    private final List<ReservationObserver> observers = new ArrayList<>();

    // Add a new observer to the list - like subscribing to notifications
    public void addObserver(ReservationObserver observer) { observers.add(observer); }

    // Remove an observer from the list - like unsubscribing from notifications
    public void removeObserver(ReservationObserver observer) { observers.remove(observer); }

    // Loop through all observers and tell each one that a reservation was created
    // This is the key part of the Observer pattern - one event triggers multiple notifications
    public void notifyCreated(Reservation reservation) {
        for (ReservationObserver obs : observers) obs.onReservationCreated(reservation);
    }

    // Loop through all observers and tell each one that a reservation was cancelled
    public void notifyCancelled(Reservation reservation) {
        for (ReservationObserver obs : observers) obs.onReservationCancelled(reservation);
    }

    // Returns how many observers are currently subscribed - useful for testing
    public int getObserverCount() { return observers.size(); }
}
