package com.oceanview.service;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Reservation;
import com.oceanview.model.StandardRoom;
import com.oceanview.model.Room;
import com.oceanview.pattern.observer.ReservationNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ReservationServiceTest — Tests ReservationService with mocked DAOs.
 * Verifies business logic: reservation creation, cancellation, search.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock private ReservationDAO reservationDAO;
    @Mock private RoomDAO roomDAO;
    @Mock private ReservationNotifier notifier;

    private ReservationService service;

    @BeforeEach
    void setUp() {
        service = new ReservationService(reservationDAO, roomDAO, notifier);
    }

    private Reservation createTestReservation() {
        Reservation r = new Reservation();
        r.setGuestName("Test Guest");
        r.setContactNumber("+94771234567");
        r.setRoomId(1);
        r.setCheckInDate(LocalDate.of(2025, 8, 1));
        r.setCheckOutDate(LocalDate.of(2025, 8, 4));
        r.setCreatedBy(1);
        return r;
    }

    @Test @DisplayName("TC-RS001: Create reservation with valid data returns true")
    void createReservationSuccess() {
        Room room = new StandardRoom();
        room.setRoomId(1);
        room.setRoomType("STANDARD");
        when(roomDAO.getRoomById(1)).thenReturn(room);
        when(reservationDAO.addReservation(any())).thenReturn(true);

        boolean result = service.createReservation(createTestReservation());
        assertTrue(result);
        verify(notifier).notifyCreated(any());
    }

    @Test @DisplayName("TC-RS002: Create reservation with invalid room returns false")
    void createReservationInvalidRoom() {
        when(roomDAO.getRoomById(1)).thenReturn(null);

        boolean result = service.createReservation(createTestReservation());
        assertFalse(result);
        verify(notifier, never()).notifyCreated(any());
    }

    @Test @DisplayName("TC-RS003: Cancel reservation works")
    void cancelReservation() {
        Reservation r = createTestReservation();
        r.setReservationId(1);
        when(reservationDAO.getReservationById(1)).thenReturn(r);
        when(reservationDAO.deleteReservation(1)).thenReturn(true);

        assertTrue(service.cancelReservation(1));
        verify(notifier).notifyCancelled(any());
    }

    @Test @DisplayName("TC-RS004: Cancel non-existent reservation returns false")
    void cancelNonExistent() {
        when(reservationDAO.getReservationById(999)).thenReturn(null);
        assertFalse(service.cancelReservation(999));
    }

    @Test @DisplayName("TC-RS005: Search delegates to DAO")
    void searchDelegates() {
        service.searchReservations("John");
        verify(reservationDAO).searchReservations("John");
    }

    @Test @DisplayName("TC-RS006: Get all delegates to DAO")
    void getAllDelegates() {
        service.getAllReservations();
        verify(reservationDAO).getAllReservations();
    }
}
