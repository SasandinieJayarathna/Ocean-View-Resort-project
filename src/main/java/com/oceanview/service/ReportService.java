package com.oceanview.service;

import com.oceanview.util.DBConnectionManager;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReportService — Generates business reports using stored procedures.
 * Demonstrates advanced database features (stored procedures).
 */
public class ReportService {

    /** Calls sp_occupancy_report stored procedure. */
    public List<Map<String, Object>> getOccupancyReport(LocalDate start, LocalDate end) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_occupancy_report(?, ?)}")) {
            cs.setDate(1, Date.valueOf(start));
            cs.setDate(2, Date.valueOf(end));
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("roomType", rs.getString("room_type"));
                row.put("totalRooms", rs.getInt("total_rooms"));
                row.put("totalBookings", rs.getInt("total_bookings"));
                row.put("occupancyPercent", rs.getDouble("occupancy_pct"));
                results.add(row);
            }
        } catch (SQLException e) { System.err.println("Occupancy report error: " + e.getMessage()); }
        return results;
    }

    /** Calls sp_revenue_report stored procedure. */
    public List<Map<String, Object>> getRevenueReport(LocalDate start, LocalDate end) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_revenue_report(?, ?)}")) {
            cs.setDate(1, Date.valueOf(start));
            cs.setDate(2, Date.valueOf(end));
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("roomType", rs.getString("room_type"));
                row.put("billsCount", rs.getInt("bills_count"));
                row.put("totalRevenue", rs.getDouble("total_revenue"));
                row.put("avgBill", rs.getDouble("avg_bill"));
                results.add(row);
            }
        } catch (SQLException e) { System.err.println("Revenue report error: " + e.getMessage()); }
        return results;
    }
}
