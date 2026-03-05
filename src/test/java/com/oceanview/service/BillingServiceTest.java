package com.oceanview.service;

import com.oceanview.dao.BillDAO;
import com.oceanview.dao.ReservationDAO;
import com.oceanview.dao.RoomDAO;
import com.oceanview.model.Bill;
import com.oceanview.model.Reservation;
import com.oceanview.model.StandardRoom;
import com.oceanview.model.Room;
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
 * BillingServiceTest — Tests BillingService with mocked DAOs.
 * Verifies Strategy pattern integration and bill generation.
 */
@ExtendWith(MockitoExtension.class)
class BillingServiceTest {
    @Mock private BillDAO billDAO;
    @Mock private ReservationDAO reservationDAO;
    @Mock private RoomDAO roomDAO;

    private BillingService service;

    @BeforeEach
    void setUp() {
        service = new BillingService(billDAO, reservationDAO, roomDAO);
    }

    private void setupMocks() {
        Reservation r = new Reservation();
        r.setReservationId(1);
        r.setRoomId(1);
        r.setCheckInDate(LocalDate.of(2025, 8, 1));
        r.setCheckOutDate(LocalDate.of(2025, 8, 4)); // 3 nights
        when(reservationDAO.getReservationById(1)).thenReturn(r);

        Room room = new StandardRoom();
        room.setRoomId(1);
        room.setPricePerNight(5000);
        when(roomDAO.getRoomById(1)).thenReturn(room);
        when(billDAO.saveBill(any())).thenReturn(true);
    }

    @Test @DisplayName("TC-BS001: Standard strategy bill")
    void standardBill() {
        setupMocks();
        Bill bill = service.generateBill(1, "STANDARD", 1);
        assertNotNull(bill);
        assertEquals("STANDARD", bill.getBillingStrategy());
        assertEquals(3, bill.getNumberOfNights());
        verify(billDAO).saveBill(any());
    }

    @Test @DisplayName("TC-BS002: Seasonal strategy bill")
    void seasonalBill() {
        setupMocks();
        Bill bill = service.generateBill(1, "SEASONAL", 1);
        assertNotNull(bill);
        assertEquals("SEASONAL", bill.getBillingStrategy());
    }

    @Test @DisplayName("TC-BS003: Loyalty strategy bill")
    void loyaltyBill() {
        setupMocks();
        Bill bill = service.generateBill(1, "LOYALTY", 1);
        assertNotNull(bill);
        assertEquals("LOYALTY", bill.getBillingStrategy());
        assertEquals(10.0, bill.getDiscountPercent());
    }

    @Test @DisplayName("TC-BS004: Null reservation returns null")
    void nullReservation() {
        when(reservationDAO.getReservationById(999)).thenReturn(null);
        Bill bill = service.generateBill(999, "STANDARD", 1);
        assertNull(bill);
    }

    @Test @DisplayName("TC-BS005: Null room returns null")
    void nullRoom() {
        Reservation r = new Reservation();
        r.setReservationId(1);
        r.setRoomId(99);
        when(reservationDAO.getReservationById(1)).thenReturn(r);
        when(roomDAO.getRoomById(99)).thenReturn(null);

        Bill bill = service.generateBill(1, "STANDARD", 1);
        assertNull(bill);
    }
}
