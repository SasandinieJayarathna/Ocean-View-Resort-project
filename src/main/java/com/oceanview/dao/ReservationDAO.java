package com.oceanview.dao;

import com.oceanview.model.Reservation;
import java.time.LocalDate;
import java.util.List;

/**
 * ReservationDAO - This is an interface for Reservation data access.
 * An interface defines what methods the implementation class must have,
 * but does not contain the actual code for those methods.
 *
 * PATTERN: DAO (Data Access Object) - separates database code from business logic.
 */
public interface ReservationDAO {

    // This method adds a new reservation to the database and returns true if successful
    boolean addReservation(Reservation reservation);

    // This method finds and returns a reservation by its unique ID number
    Reservation getReservationById(int id);

    // This method finds and returns a reservation by its reservation number (e.g., "RES-0001")
    Reservation getReservationByNumber(String number);

    // This method returns a list of all reservations from the database
    List<Reservation> getAllReservations();

    // This method searches reservations by a keyword (matches guest name, reservation number, or phone)
    List<Reservation> searchReservations(String keyword);

    // This method returns reservations that fall within a given date range
    List<Reservation> getReservationsByDateRange(LocalDate start, LocalDate end);

    // This method updates an existing reservation's details and returns true if successful
    boolean updateReservation(Reservation reservation);

    // This method cancels a reservation by its ID (soft delete - just changes status)
    boolean deleteReservation(int id);

    // This method counts how many reservations were made today
    int getTodayReservationCount();
}
