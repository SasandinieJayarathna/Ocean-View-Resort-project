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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ReservationDAOImplExtendedTest — Additional tests covering
 * getAllReservations, getReservationsByDateRange, updateReservation,
 * deleteReservation, and getTodayReservationCount.
 */
@ExtendWith(MockitoExtension.class)
class ReservationDAOImplExtendedTest {
    @Mock private Connection connection;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;
    @Mock private Statement statement;

    private ReservationDAOImpl reservationDAO;

    @BeforeEach
    void setUp() throws SQLException {
        reservationDAO = new ReservationDAOImpl(connection);
        lenient().when(connection.prepareStatement(any(String.class))).thenReturn(ps);
        lenient().when(connection.prepareStatement(any(String.class), anyInt())).thenReturn(ps);
        lenient().when(connection.createStatement()).thenReturn(statement);
    }

    private void mockReservationRow() throws SQLException {
        when(rs.getInt("reservation_id")).thenReturn(1);
        when(rs.getString("reservation_number")).thenReturn("RES-100001");
        when(rs.getString("guest_name")).thenReturn("Jane Doe");
        when(rs.getString("guest_address")).thenReturn("456 Sea Ave");
        when(rs.getString("contact_number")).thenReturn("+94779876543");
        when(rs.getString("guest_email")).thenReturn("jane@test.com");
        when(rs.getInt("room_id")).thenReturn(2);
        when(rs.getString("room_type")).thenReturn("DELUXE");
        when(rs.getDate("check_in_date")).thenReturn(Date.valueOf("2025-08-01"));
        when(rs.getDate("check_out_date")).thenReturn(Date.valueOf("2025-08-05"));
        when(rs.getString("status")).thenReturn("CONFIRMED");
        when(rs.getString("special_requests")).thenReturn("Late checkout");
        when(rs.getInt("created_by")).thenReturn(1);
        when(rs.getTimestamp("created_at")).thenReturn(null);
    }

    @Test @DisplayName("TC-RDE001: getAllReservations returns list")
    void getAllReservations() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockReservationRow();

        List<Reservation> list = reservationDAO.getAllReservations();
        assertEquals(1, list.size());
        assertEquals("Jane Doe", list.get(0).getGuestName());
    }

    @Test @DisplayName("TC-RDE002: getAllReservations returns empty list")
    void getAllReservationsEmpty() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertTrue(reservationDAO.getAllReservations().isEmpty());
    }

    @Test @DisplayName("TC-RDE003: getReservationsByDateRange returns matching")
    void getReservationsByDateRange() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockReservationRow();

        List<Reservation> list = reservationDAO.getReservationsByDateRange(
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 31));
        assertEquals(1, list.size());
        assertEquals("DELUXE", list.get(0).getRoomType());
    }

    @Test @DisplayName("TC-RDE004: getReservationsByDateRange returns empty")
    void getReservationsByDateRangeEmpty() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertTrue(reservationDAO.getReservationsByDateRange(
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 2)).isEmpty());
    }

    @Test @DisplayName("TC-RDE005: updateReservation returns true on success")
    void updateReservationSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        Reservation r = new Reservation();
        r.setReservationId(1);
        r.setGuestName("Updated Name");
        r.setGuestAddress("New Address");
        r.setContactNumber("+94771111111");
        r.setGuestEmail("updated@test.com");
        r.setStatus("CHECKED_IN");
        r.setSpecialRequests("Extra pillows");
        assertTrue(reservationDAO.updateReservation(r));
    }

    @Test @DisplayName("TC-RDE006: updateReservation returns false on failure")
    void updateReservationFailure() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        Reservation r = new Reservation();
        r.setReservationId(999);
        assertFalse(reservationDAO.updateReservation(r));
    }

    @Test @DisplayName("TC-RDE007: deleteReservation returns true on success")
    void deleteReservationSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        assertTrue(reservationDAO.deleteReservation(1));
    }

    @Test @DisplayName("TC-RDE008: deleteReservation returns false on failure")
    void deleteReservationFailure() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        assertFalse(reservationDAO.deleteReservation(999));
    }

    @Test @DisplayName("TC-RDE009: getTodayReservationCount returns count")
    void getTodayReservationCount() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(5);
        assertEquals(5, reservationDAO.getTodayReservationCount());
    }

    @Test @DisplayName("TC-RDE010: getTodayReservationCount returns 0 on exception")
    void getTodayReservationCountException() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenThrow(new SQLException("error"));
        assertEquals(0, reservationDAO.getTodayReservationCount());
    }

    @Test @DisplayName("TC-RDE011: addReservation handles SQL exception")
    void addReservationException() throws SQLException {
        when(ps.executeUpdate()).thenThrow(new SQLException("DB error"));
        Reservation r = new Reservation();
        r.setCheckInDate(LocalDate.of(2025, 8, 1));
        r.setCheckOutDate(LocalDate.of(2025, 8, 4));
        assertFalse(reservationDAO.addReservation(r));
    }

    @Test @DisplayName("TC-RDE012: Reservation with created_at timestamp is mapped")
    void reservationWithTimestamp() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("reservation_id")).thenReturn(2);
        when(rs.getString("reservation_number")).thenReturn("RES-100002");
        when(rs.getString("guest_name")).thenReturn("Bob");
        when(rs.getString("guest_address")).thenReturn("789 Coral St");
        when(rs.getString("contact_number")).thenReturn("+94770000000");
        when(rs.getString("guest_email")).thenReturn("bob@test.com");
        when(rs.getInt("room_id")).thenReturn(3);
        when(rs.getString("room_type")).thenReturn("SUITE");
        when(rs.getDate("check_in_date")).thenReturn(Date.valueOf("2025-09-01"));
        when(rs.getDate("check_out_date")).thenReturn(Date.valueOf("2025-09-05"));
        when(rs.getString("status")).thenReturn("CONFIRMED");
        when(rs.getString("special_requests")).thenReturn(null);
        when(rs.getInt("created_by")).thenReturn(2);
        Timestamp ts = Timestamp.valueOf("2025-08-20 14:30:00");
        when(rs.getTimestamp("created_at")).thenReturn(ts);

        Reservation r = reservationDAO.getReservationById(2);
        assertNotNull(r);
        assertNotNull(r.getCreatedAt());
        assertEquals("SUITE", r.getRoomType());
    }

    @Test @DisplayName("TC-RDE013: updateReservation handles SQL exception")
    void updateReservationException() throws SQLException {
        when(ps.executeUpdate()).thenThrow(new SQLException("DB error"));
        Reservation r = new Reservation();
        r.setReservationId(1);
        assertFalse(reservationDAO.updateReservation(r));
    }

    @Test @DisplayName("TC-RDE014: deleteReservation handles SQL exception")
    void deleteReservationException() throws SQLException {
        when(ps.executeUpdate()).thenThrow(new SQLException("DB error"));
        assertFalse(reservationDAO.deleteReservation(1));
    }
}
