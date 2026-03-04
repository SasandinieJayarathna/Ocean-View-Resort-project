package com.oceanview.dao;

import com.oceanview.model.Reservation;
import com.oceanview.util.DBConnectionManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ReservationDAOImpl — JDBC implementation of ReservationDAO.
 * PATTERN: DAO — isolates all reservation SQL. SOLID: Single Responsibility.
 */
public class ReservationDAOImpl implements ReservationDAO {
    private Connection connection;

    public ReservationDAOImpl(Connection connection) { this.connection = connection; }

    public ReservationDAOImpl() {
        try { this.connection = DBConnectionManager.getInstance().getConnection(); }
        catch (SQLException e) { throw new RuntimeException("Failed to get DB connection", e); }
    }

    @Override
    public boolean addReservation(Reservation r) {
        String sql = "INSERT INTO reservations (reservation_number, guest_name, guest_address, contact_number, guest_email, room_id, room_type, check_in_date, check_out_date, status, special_requests, created_by) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getReservationNumber());
            ps.setString(2, r.getGuestName());
            ps.setString(3, r.getGuestAddress());
            ps.setString(4, r.getContactNumber());
            ps.setString(5, r.getGuestEmail());
            ps.setInt(6, r.getRoomId());
            ps.setString(7, r.getRoomType());
            ps.setDate(8, Date.valueOf(r.getCheckInDate()));
            ps.setDate(9, Date.valueOf(r.getCheckOutDate()));
            ps.setString(10, r.getStatus());
            ps.setString(11, r.getSpecialRequests());
            ps.setInt(12, r.getCreatedBy());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) r.setReservationId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.err.println("Error adding reservation: " + e.getMessage()); }
        return false;
    }

    @Override
    public Reservation getReservationById(int id) {
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    @Override
    public Reservation getReservationByNumber(String number) {
        String sql = "SELECT * FROM reservations WHERE reservation_number = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, number);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    @Override
    public List<Reservation> getAllReservations() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY created_at DESC";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return list;
    }

    @Override
    public List<Reservation> searchReservations(String keyword) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE guest_name LIKE ? OR reservation_number LIKE ? OR contact_number LIKE ? ORDER BY created_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String p = "%" + keyword + "%";
            ps.setString(1, p); ps.setString(2, p); ps.setString(3, p);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return list;
    }

    @Override
    public List<Reservation> getReservationsByDateRange(LocalDate start, LocalDate end) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE check_in_date >= ? AND check_out_date <= ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start)); ps.setDate(2, Date.valueOf(end));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return list;
    }

    @Override
    public boolean updateReservation(Reservation r) {
        String sql = "UPDATE reservations SET guest_name=?, guest_address=?, contact_number=?, guest_email=?, status=?, special_requests=? WHERE reservation_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, r.getGuestName()); ps.setString(2, r.getGuestAddress());
            ps.setString(3, r.getContactNumber()); ps.setString(4, r.getGuestEmail());
            ps.setString(5, r.getStatus()); ps.setString(6, r.getSpecialRequests());
            ps.setInt(7, r.getReservationId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return false;
    }

    @Override
    public boolean deleteReservation(int id) {
        String sql = "UPDATE reservations SET status='CANCELLED' WHERE reservation_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return false;
    }

    @Override
    public int getTodayReservationCount() {
        String sql = "SELECT COUNT(*) FROM reservations WHERE DATE(created_at) = CURDATE()";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return 0;
    }

    private Reservation mapRow(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setReservationNumber(rs.getString("reservation_number"));
        r.setGuestName(rs.getString("guest_name"));
        r.setGuestAddress(rs.getString("guest_address"));
        r.setContactNumber(rs.getString("contact_number"));
        r.setGuestEmail(rs.getString("guest_email"));
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomType(rs.getString("room_type"));
        r.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        r.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());
        r.setStatus(rs.getString("status"));
        r.setSpecialRequests(rs.getString("special_requests"));
        r.setCreatedBy(rs.getInt("created_by"));
        if (rs.getTimestamp("created_at") != null) r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return r;
    }
}
