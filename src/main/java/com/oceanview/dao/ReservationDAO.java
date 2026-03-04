package com.oceanview.dao;

import com.oceanview.model.Reservation;
import java.time.LocalDate;
import java.util.List;

/**
 * ReservationDAO — Data access interface for Reservation operations.
 */
public interface ReservationDAO {
    boolean addReservation(Reservation reservation);
    Reservation getReservationById(int id);
    Reservation getReservationByNumber(String number);
    List<Reservation> getAllReservations();
    List<Reservation> searchReservations(String keyword);
    List<Reservation> getReservationsByDateRange(LocalDate start, LocalDate end);
    boolean updateReservation(Reservation reservation);
    boolean deleteReservation(int id);
    int getTodayReservationCount();
}
