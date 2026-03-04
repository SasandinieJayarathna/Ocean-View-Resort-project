package com.oceanview.service;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Reservation;
import com.oceanview.model.Room;
import com.oceanview.pattern.observer.ReservationNotifier;
import com.oceanview.util.ReservationNumberGenerator;
import java.util.List;

/**
 * ReservationService — Business logic for reservations.
 * SOLID: Single Responsibility — only reservation business logic.
 * SOLID: Dependency Inversion — depends on DAO interfaces.
 * PATTERN: Uses Observer (ReservationNotifier) for notifications.
 */
public class ReservationService {
    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final ReservationNotifier notifier;

    public ReservationService(ReservationDAO reservationDAO, RoomDAO roomDAO, ReservationNotifier notifier) {
        this.reservationDAO = reservationDAO;
        this.roomDAO = roomDAO;
        this.notifier = notifier;
    }

    /** Creates a new reservation with auto-generated reservation number. */
    public boolean createReservation(Reservation reservation) {
        // Generate unique reservation number
        reservation.setReservationNumber(ReservationNumberGenerator.generateNext());

        // Verify room exists
        Room room = roomDAO.getRoomById(reservation.getRoomId());
        if (room == null) return false;

        reservation.setRoomType(room.getRoomType());
        boolean result = reservationDAO.addReservation(reservation);

        // Notify observers (Observer pattern)
        if (result && notifier != null) {
            notifier.notifyCreated(reservation);
        }
        return result;
    }

    public Reservation getReservationByNumber(String number) {
        return reservationDAO.getReservationByNumber(number);
    }

    public Reservation getReservationById(int id) {
        return reservationDAO.getReservationById(id);
    }

    public List<Reservation> getAllReservations() {
        return reservationDAO.getAllReservations();
    }

    public List<Reservation> searchReservations(String keyword) {
        return reservationDAO.searchReservations(keyword);
    }

    /** Cancels a reservation and notifies observers. */
    public boolean cancelReservation(int id) {
        Reservation r = reservationDAO.getReservationById(id);
        if (r == null) return false;
        boolean result = reservationDAO.deleteReservation(id);
        if (result && notifier != null) {
            notifier.notifyCancelled(r);
        }
        return result;
    }

    public int getTodayCount() {
        return reservationDAO.getTodayReservationCount();
    }
}
