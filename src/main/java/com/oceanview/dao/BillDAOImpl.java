package com.oceanview.dao;

import com.oceanview.model.Bill;
import com.oceanview.util.DBConnectionManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BillDAOImpl — JDBC implementation of BillDAO.
 * PATTERN: DAO — isolates all bill-related SQL.
 * SECURITY: Uses PreparedStatement to prevent SQL injection.
 */
public class BillDAOImpl implements BillDAO {
    private Connection connection;

    public BillDAOImpl(Connection connection) { this.connection = connection; }

    public BillDAOImpl() {
        try { this.connection = DBConnectionManager.getInstance().getConnection(); }
        catch (SQLException e) { throw new RuntimeException("Failed to get DB connection", e); }
    }

    @Override
    public boolean saveBill(Bill bill) {
        String sql = "INSERT INTO bills (reservation_id, number_of_nights, room_rate, subtotal, tax_rate, tax_amount, discount_percent, discount_amount, total_amount, billing_strategy, generated_by) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bill.getReservationId());
            ps.setInt(2, bill.getNumberOfNights());
            ps.setDouble(3, bill.getRoomRate());
            ps.setDouble(4, bill.getSubtotal());
            ps.setDouble(5, bill.getTaxRate());
            ps.setDouble(6, bill.getTaxAmount());
            ps.setDouble(7, bill.getDiscountPercent());
            ps.setDouble(8, bill.getDiscountAmount());
            ps.setDouble(9, bill.getTotalAmount());
            ps.setString(10, bill.getBillingStrategy());
            ps.setInt(11, bill.getGeneratedBy());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) bill.setBillId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.err.println("Error saving bill: " + e.getMessage()); }
        return false;
    }

    @Override
    public Bill getBillByReservationId(int reservationId) {
        String sql = "SELECT * FROM bills WHERE reservation_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToBill(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    @Override
    public List<Bill> getAllBills() {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills ORDER BY generated_at DESC";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToBill(rs));
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return list;
    }

    @Override
    public List<Bill> getBillsByDateRange(LocalDate start, LocalDate end) {
        List<Bill> list = new ArrayList<>();
        String sql = "SELECT * FROM bills WHERE generated_at BETWEEN ? AND ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapResultSetToBill(rs));
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return list;
    }

    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setReservationId(rs.getInt("reservation_id"));
        bill.setNumberOfNights(rs.getInt("number_of_nights"));
        bill.setRoomRate(rs.getDouble("room_rate"));
        bill.setSubtotal(rs.getDouble("subtotal"));
        bill.setTaxRate(rs.getDouble("tax_rate"));
        bill.setTaxAmount(rs.getDouble("tax_amount"));
        bill.setDiscountPercent(rs.getDouble("discount_percent"));
        bill.setDiscountAmount(rs.getDouble("discount_amount"));
        bill.setTotalAmount(rs.getDouble("total_amount"));
        bill.setBillingStrategy(rs.getString("billing_strategy"));
        if (rs.getTimestamp("generated_at") != null)
            bill.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        bill.setGeneratedBy(rs.getInt("generated_by"));
        return bill;
    }
}
