package com.oceanview.dao;

import com.oceanview.model.Room;
import com.oceanview.pattern.factory.RoomFactory;
import com.oceanview.util.DBConnectionManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAOImpl — JDBC implementation of RoomDAO.
 * PATTERN: DAO — isolates all room-related SQL.
 * FEATURE: getAvailableRooms() calls stored procedure sp_get_available_rooms.
 * USES: RoomFactory to create correct Room subtype from database results.
 */
public class RoomDAOImpl implements RoomDAO {
    private Connection connection;

    public RoomDAOImpl(Connection connection) { this.connection = connection; }

    public RoomDAOImpl() {
        try { this.connection = DBConnectionManager.getInstance().getConnection(); }
        catch (SQLException e) { throw new RuntimeException("Failed to get DB connection", e); }
    }

    @Override
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, description, max_occupancy) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setDouble(3, room.getPricePerNight());
            ps.setString(4, room.getDescription());
            ps.setInt(5, room.getMaxOccupancy());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) room.setRoomId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.err.println("Error adding room: " + e.getMessage()); }
        return false;
    }

    @Override
    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE room_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToRoom(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    @Override
    public Room getRoomByNumber(String number) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToRoom(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    @Override
    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY room_number";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToRoom(rs));
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return list;
    }

    /**
     * Calls stored procedure sp_get_available_rooms to find available rooms.
     * Uses CallableStatement for stored procedure invocation.
     */
    @Override
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, String type) {
        List<Room> list = new ArrayList<>();
        try (CallableStatement cs = connection.prepareCall("{CALL sp_get_available_rooms(?, ?, ?)}")) {
            cs.setDate(1, Date.valueOf(checkIn));
            cs.setDate(2, Date.valueOf(checkOut));
            if (type != null && !type.isEmpty()) {
                cs.setString(3, type);
            } else {
                cs.setNull(3, Types.VARCHAR);
            }
            ResultSet rs = cs.executeQuery();
            while (rs.next()) list.add(mapResultSetToRoom(rs));
        } catch (SQLException e) { System.err.println("Error getting available rooms: " + e.getMessage()); }
        return list;
    }

    @Override
    public boolean updateRoom(Room room) {
        String sql = "UPDATE rooms SET price_per_night=?, is_available=?, description=?, max_occupancy=? WHERE room_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDouble(1, room.getPricePerNight());
            ps.setBoolean(2, room.isAvailable());
            ps.setString(3, room.getDescription());
            ps.setInt(4, room.getMaxOccupancy());
            ps.setInt(5, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return false;
    }

    /**
     * Maps a ResultSet row to the correct Room subtype using RoomFactory.
     * PATTERN: Uses Factory to create StandardRoom, DeluxeRoom, or SuiteRoom.
     */
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        String roomType = rs.getString("room_type");
        Room room = RoomFactory.createRoom(roomType);
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setPricePerNight(rs.getDouble("price_per_night"));
        room.setAvailable(rs.getBoolean("is_available"));
        room.setDescription(rs.getString("description"));
        room.setMaxOccupancy(rs.getInt("max_occupancy"));
        return room;
    }
}
