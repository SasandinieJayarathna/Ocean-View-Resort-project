package com.oceanview.pattern.observer;

import com.oceanview.model.Reservation;
import java.util.ArrayList;
import java.util.List;

/**
 * ReservationNotifier — Subject in the Observer pattern.
 * Maintains a list of observers and notifies them of reservation events.
 * PATTERN: Observer — decouples reservation logic from notification channels.
 */
public class ReservationNotifier {
    private final List<ReservationObserver> observers = new ArrayList<>();

    public void addObserver(ReservationObserver observer) { observers.add(observer); }
    public void removeObserver(ReservationObserver observer) { observers.remove(observer); }

    /** Notify all observers that a reservation was created. */
    public void notifyCreated(Reservation reservation) {
        for (ReservationObserver obs : observers) obs.onReservationCreated(reservation);
    }

    /** Notify all observers that a reservation was cancelled. */
    public void notifyCancelled(Reservation reservation) {
        for (ReservationObserver obs : observers) obs.onReservationCancelled(reservation);
    }

    public int getObserverCount() { return observers.size(); }
}
