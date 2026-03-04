package com.oceanview.dao;

import com.oceanview.model.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ReservationDAOImplTest — Mockito-based tests for ReservationDAOImpl.
 * Mocks JDBC to test DAO logic without a real database.
 */
@ExtendWith(MockitoExtension.class)
class ReservationDAOImplTest {
    @Mock private Connection connection;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;
    @Mock private ResultSet generatedKeys;

    private ReservationDAOImpl reservationDAO;

    @BeforeEach
    void setUp() throws SQLException {
        reservationDAO = new ReservationDAOImpl(connection);
        lenient().when(connection.prepareStatement(any(String.class))).thenReturn(ps);
        lenient().when(connection.prepareStatement(any(String.class), anyInt())).thenReturn(ps);
    }

    private void mockReservationRow() throws SQLException {
        when(rs.getInt("reservation_id")).thenReturn(1);
        when(rs.getString("reservation_number")).thenReturn("RES-100001");
        when(rs.getString("guest_name")).thenReturn("John Smith");
        when(rs.getString("guest_address")).thenReturn("123 Beach Rd");
        when(rs.getString("contact_number")).thenReturn("+94771234567");
        when(rs.getString("guest_email")).thenReturn("john@test.com");
        when(rs.getInt("room_id")).thenReturn(1);
        when(rs.getString("room_type")).thenReturn("STANDARD");
        when(rs.getDate("check_in_date")).thenReturn(Date.valueOf("2025-08-01"));
        when(rs.getDate("check_out_date")).thenReturn(Date.valueOf("2025-08-04"));
        when(rs.getString("status")).thenReturn("CONFIRMED");
        when(rs.getString("special_requests")).thenReturn(null);
        when(rs.getInt("created_by")).thenReturn(1);
        when(rs.getTimestamp("created_at")).thenReturn(null);
    }

    @Test @DisplayName("TC-D005: addReservation returns true on success")
    void addReservationSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(10);

        Reservation r = new Reservation();
        r.setReservationNumber("RES-100001");
        r.setGuestName("John Smith");
        r.setContactNumber("+94771234567");
        r.setRoomId(1);
        r.setRoomType("STANDARD");
        r.setCheckInDate(LocalDate.of(2025, 8, 1));
        r.setCheckOutDate(LocalDate.of(2025, 8, 4));
        r.setCreatedBy(1);

        assertTrue(reservationDAO.addReservation(r));
        assertEquals(10, r.getReservationId());
    }

    @Test @DisplayName("TC-D006: getReservationById returns reservation when found")
    void getReservationByIdFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockReservationRow();

        Reservation r = reservationDAO.getReservationById(1);
        assertNotNull(r);
        assertEquals("RES-100001", r.getReservationNumber());
        assertEquals("John Smith", r.getGuestName());
    }

    @Test @DisplayName("TC-D007: getReservationByNumber returns reservation when found")
    void getReservationByNumberFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockReservationRow();

        Reservation r = reservationDAO.getReservationByNumber("RES-100001");
        assertNotNull(r);
        assertEquals("STANDARD", r.getRoomType());
    }

    @Test @DisplayName("TC-D008: getReservationById returns null when not found")
    void getReservationByIdNotFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        assertNull(reservationDAO.getReservationById(999));
    }

    @Test @DisplayName("TC-D009: searchReservations returns matching results")
    void searchReservations() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockReservationRow();

        var results = reservationDAO.searchReservations("John");
        assertEquals(1, results.size());
        assertEquals("John Smith", results.get(0).getGuestName());
    }
}
