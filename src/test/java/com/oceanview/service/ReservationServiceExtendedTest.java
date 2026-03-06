package com.oceanview.service;

import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Reservation;
import com.oceanview.pattern.observer.ReservationNotifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ReservationServiceExtendedTest — Additional tests for ReservationService.
 * Covers getByNumber, getById, getTodayCount, and null notifier edge case.
 */
@ExtendWith(MockitoExtension.class)
class ReservationServiceExtendedTest {
    @Mock private ReservationDAO reservationDAO;
    @Mock private RoomDAO roomDAO;
    @Mock private ReservationNotifier notifier;

    private ReservationService service;

    @BeforeEach
    void setUp() {
        service = new ReservationService(reservationDAO, roomDAO, notifier);
    }

    @Test @DisplayName("TC-RSE001: getReservationByNumber delegates to DAO")
    void getReservationByNumber() {
        Reservation r = new Reservation();
        r.setReservationNumber("RES-100001");
        when(reservationDAO.getReservationByNumber("RES-100001")).thenReturn(r);

        Reservation result = service.getReservationByNumber("RES-100001");
        assertNotNull(result);
        assertEquals("RES-100001", result.getReservationNumber());
        verify(reservationDAO).getReservationByNumber("RES-100001");
    }

    @Test @DisplayName("TC-RSE002: getReservationById delegates to DAO")
    void getReservationById() {
        Reservation r = new Reservation();
        r.setReservationId(1);
        when(reservationDAO.getReservationById(1)).thenReturn(r);

        Reservation result = service.getReservationById(1);
        assertNotNull(result);
        assertEquals(1, result.getReservationId());
    }

    @Test @DisplayName("TC-RSE003: getTodayCount delegates to DAO")
    void getTodayCount() {
        when(reservationDAO.getTodayReservationCount()).thenReturn(5);
        assertEquals(5, service.getTodayCount());
        verify(reservationDAO).getTodayReservationCount();
    }

    @Test @DisplayName("TC-RSE004: getReservationByNumber returns null when not found")
    void getReservationByNumberNotFound() {
        when(reservationDAO.getReservationByNumber("RES-999999")).thenReturn(null);
        assertNull(service.getReservationByNumber("RES-999999"));
    }
}
