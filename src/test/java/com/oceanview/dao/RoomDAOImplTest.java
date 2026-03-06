package com.oceanview.dao;

import com.oceanview.model.Room;
import com.oceanview.model.StandardRoom;
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
 * RoomDAOImplTest — Mockito-based tests for RoomDAOImpl.
 * Mocks JDBC to test DAO logic without a real database.
 */
@ExtendWith(MockitoExtension.class)
class RoomDAOImplTest {
    @Mock private Connection connection;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;
    @Mock private ResultSet generatedKeys;
    @Mock private Statement statement;
    @Mock private CallableStatement cs;

    private RoomDAOImpl roomDAO;

    @BeforeEach
    void setUp() throws SQLException {
        roomDAO = new RoomDAOImpl(connection);
        lenient().when(connection.prepareStatement(any(String.class))).thenReturn(ps);
        lenient().when(connection.prepareStatement(any(String.class), anyInt())).thenReturn(ps);
        lenient().when(connection.createStatement()).thenReturn(statement);
        lenient().when(connection.prepareCall(any(String.class))).thenReturn(cs);
    }

    private void mockRoomRow(ResultSet mockRs) throws SQLException {
        when(mockRs.getInt("room_id")).thenReturn(1);
        when(mockRs.getString("room_number")).thenReturn("101");
        when(mockRs.getString("room_type")).thenReturn("STANDARD");
        when(mockRs.getDouble("price_per_night")).thenReturn(5000.0);
        when(mockRs.getBoolean("is_available")).thenReturn(true);
        when(mockRs.getString("description")).thenReturn("Ocean view room");
        when(mockRs.getInt("max_occupancy")).thenReturn(2);
    }

    @Test @DisplayName("TC-RD001: addRoom returns true on success")
    void addRoomSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(10);

        Room room = new StandardRoom();
        room.setRoomNumber("201");
        room.setPricePerNight(7500.0);
        room.setDescription("New room");
        room.setMaxOccupancy(3);

        assertTrue(roomDAO.addRoom(room));
        assertEquals(10, room.getRoomId());
    }

    @Test @DisplayName("TC-RD002: addRoom returns false on failure")
    void addRoomFailure() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        Room room = new StandardRoom();
        assertFalse(roomDAO.addRoom(room));
    }

    @Test @DisplayName("TC-RD003: getRoomById returns room when found")
    void getRoomByIdFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockRoomRow(rs);

        Room room = roomDAO.getRoomById(1);
        assertNotNull(room);
        assertEquals("101", room.getRoomNumber());
        assertEquals(5000.0, room.getPricePerNight());
    }

    @Test @DisplayName("TC-RD004: getRoomById returns null when not found")
    void getRoomByIdNotFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertNull(roomDAO.getRoomById(999));
    }

    @Test @DisplayName("TC-RD005: getRoomByNumber returns room when found")
    void getRoomByNumberFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockRoomRow(rs);

        Room room = roomDAO.getRoomByNumber("101");
        assertNotNull(room);
        assertEquals("STANDARD", room.getRoomType());
    }

    @Test @DisplayName("TC-RD006: getRoomByNumber returns null when not found")
    void getRoomByNumberNotFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertNull(roomDAO.getRoomByNumber("999"));
    }

    @Test @DisplayName("TC-RD007: getAllRooms returns list")
    void getAllRooms() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockRoomRow(rs);

        List<Room> rooms = roomDAO.getAllRooms();
        assertEquals(1, rooms.size());
        assertEquals("101", rooms.get(0).getRoomNumber());
    }

    @Test @DisplayName("TC-RD008: getAllRooms returns empty list when none")
    void getAllRoomsEmpty() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<Room> rooms = roomDAO.getAllRooms();
        assertTrue(rooms.isEmpty());
    }

    @Test @DisplayName("TC-RD009: getAvailableRooms with type")
    void getAvailableRoomsWithType() throws SQLException {
        when(cs.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockRoomRow(rs);

        List<Room> rooms = roomDAO.getAvailableRooms(
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 4), "STANDARD");
        assertEquals(1, rooms.size());
        verify(cs).setString(3, "STANDARD");
    }

    @Test @DisplayName("TC-RD010: getAvailableRooms without type sets null")
    void getAvailableRoomsNoType() throws SQLException {
        when(cs.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        List<Room> rooms = roomDAO.getAvailableRooms(
                LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 4), null);
        assertTrue(rooms.isEmpty());
        verify(cs).setNull(3, Types.VARCHAR);
    }

    @Test @DisplayName("TC-RD011: getAvailableRooms with empty type sets null")
    void getAvailableRoomsEmptyType() throws SQLException {
        when(cs.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        roomDAO.getAvailableRooms(LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 4), "");
        verify(cs).setNull(3, Types.VARCHAR);
    }

    @Test @DisplayName("TC-RD012: updateRoom returns true on success")
    void updateRoomSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        Room room = new StandardRoom();
        room.setRoomId(1);
        room.setPricePerNight(6000.0);
        room.setDescription("Updated");
        room.setMaxOccupancy(3);
        assertTrue(roomDAO.updateRoom(room));
    }

    @Test @DisplayName("TC-RD013: updateRoom returns false on failure")
    void updateRoomFailure() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        Room room = new StandardRoom();
        room.setRoomId(999);
        assertFalse(roomDAO.updateRoom(room));
    }

    @Test @DisplayName("TC-RD014: addRoom handles SQL exception")
    void addRoomSQLException() throws SQLException {
        when(ps.executeUpdate()).thenThrow(new SQLException("DB error"));
        Room room = new StandardRoom();
        assertFalse(roomDAO.addRoom(room));
    }

    @Test @DisplayName("TC-RD015: getRoomById handles SQL exception")
    void getRoomByIdSQLException() throws SQLException {
        when(ps.executeQuery()).thenThrow(new SQLException("DB error"));
        assertNull(roomDAO.getRoomById(1));
    }
}
