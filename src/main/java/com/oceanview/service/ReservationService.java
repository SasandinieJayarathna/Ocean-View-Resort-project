package com.oceanview.service;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.pattern.observer.ReservationNotifier;
import com.oceanview.util.ReservationNumberGenerator;
import java.util.List;

/**
 * ReservationService - This class handles the business logic for hotel reservations.
 * It creates, retrieves, searches, and cancels reservations.
 *
 * SOLID: Single Responsibility - this class only deals with reservation business logic.
 * SOLID: Dependency Inversion - depends on DAO interfaces, not implementations.
 * PATTERN: Uses Observer (ReservationNotifier) to send notifications when reservations are created or cancelled.
 */
public class ReservationService {

    // These are the DAOs and notifier we use - they are injected through the constructor
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final ReservationNotifier notifier;

    // We pass in the DAOs and notifier through the constructor - this is called Dependency Injection
    // This makes the class easier to test because we can pass in fake (mock) objects
    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO, ReservationNotifier notifier) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.notifier = notifier;
    }

    /**
     * This method creates a new reservation.
     * It generates a unique reservation number, checks that the room exists,
     * saves it to the database, and notifies observers if successful.
     */
    public boolean createReservation(Reservation reservation) {
        // Generate a unique reservation number (e.g., "RES-0001")
        reservation.setReservationNumber(ReservationNumberGenerator.generateNext());

        // Verify that the room exists in the database before making the reservation
        Room room = roomDAO.getRoomById(reservation.getRoomId());

        // If the room doesn't exist, we can't make a reservation for it
        if (room == null) {
            return false;
        }

        // Set the room type from the room object (so the reservation knows what type of room it is)
        reservation.setRoomType(room.getRoomType());

        // Save the reservation to the database
        boolean result = reservationDAO.addReservation(reservation);

        // If the reservation was saved successfully, notify all observers (Observer pattern)
        // Observers might send an email, update a dashboard, log the event, etc.
        if (result && notifier != null) {
            notifier.notifyCreated(reservation);
        }

        return result;
    }

    // This method finds a reservation by its reservation number (e.g., "RES-0001")
    public Reservation getReservationByNumber(String number) {
        return reservationDAO.getReservationByNumber(number);
    }

    // This method finds a reservation by its unique ID
    public Reservation getReservationById(int id) {
        return reservationDAO.getReservationById(id);
    }

    // This method returns a list of all reservations from the database
    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    // This method searches reservations by a keyword (matches guest name, number, or phone)
    public List<Reservation> searchReservations(String keyword) {
        return reservationDAO.searchReservations(keyword);
    }

    /**
     * This method cancels a reservation and notifies observers.
     * It first checks that the reservation exists, then cancels it in the database.
     * If cancellation is successful, it uses the Observer pattern to send notifications.
     */
    public boolean cancelReservation(int id) {
        // First, look up the reservation to make sure it exists
        Reservation r = reservationDAO.getReservationById(id);

        // If the reservation doesn't exist, we can't cancel it
        if (r == null) {
            return false;
        }

        // Cancel the reservation in the database (sets status to "CANCELLED")
        boolean result = reservationDAO.deleteReservation(id);

        // If the cancellation was successful, notify all observers (Observer pattern)
        if (result && notifier != null) {
            notifier.notifyCancelled(r);
        }

        return result;
    }

    // This method returns the count of reservations created today
    public int getTodayCount() {
        return reservationDAO.getTodayReservationCount();
    }
}
