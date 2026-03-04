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
 * ReportService - This class generates business reports for the hotel.
 * It calls stored procedures in the database to get occupancy and revenue data.
 *
 * A stored procedure is a pre-written SQL program stored inside the database
 * that we can call from Java using CallableStatement.
 */
public class ReportService {

    /**
     * This method generates an occupancy report for a given date range.
     * It calls the stored procedure sp_occupancy_report in the database.
     * The report shows how many rooms of each type were booked and the occupancy percentage.
     *
     * @param start - the start date for the report
     * @param end   - the end date for the report
     * @return a list of maps, where each map is one row of the report
     */
    public List<Map<String, Object>> getOccupancyReport(LocalDate start, LocalDate end) {
        // Create an empty list to store the report rows
        List<Map<String, Object>> results = new ArrayList<>();

        // Get a database connection using our Singleton manager and call the stored procedure
        // try-with-resources automatically closes the connection and statement when done
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_occupancy_report(?, ?)}")) {

            // Set the start and end date parameters for the stored procedure
            // We convert Java LocalDate to SQL Date because the database expects SQL dates
            cs.setDate(1, Date.valueOf(start));
            cs.setDate(2, Date.valueOf(end));

            // Execute the stored procedure and get the results
            ResultSet rs = cs.executeQuery();

            // Loop through each row in the results
            while (rs.next()) {
                // Store each row as a Map (like a dictionary) with column names as keys
                Map<String, Object> row = new HashMap<>();
                row.put("roomType", rs.getString("room_type"));
                row.put("totalRooms", rs.getInt("total_rooms"));
                row.put("totalBookings", rs.getInt("total_bookings"));
                row.put("occupancyPercent", rs.getDouble("occupancy_pct"));

                // Add this row to our results list
                results.add(row);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Occupancy report error: " + e.getMessage());
        }

        // Return the report data
        return results;
    }

    /**
     * This method generates a revenue report for a given date range.
     * It calls the stored procedure sp_revenue_report in the database.
     * The report shows how much money was earned from each room type.
     *
     * @param start - the start date for the report
     * @param end   - the end date for the report
     * @return a list of maps, where each map is one row of the report
     */
    public List<Map<String, Object>> getRevenueReport(LocalDate start, LocalDate end) {
        // Create an empty list to store the report rows
        List<Map<String, Object>> results = new ArrayList<>();

        // Get a database connection using our Singleton manager and call the stored procedure
        // try-with-resources automatically closes the connection and statement when done
        try (Connection conn = DBConnectionManager.getInstance().getConnection();
             CallableStatement cs = conn.prepareCall("{CALL sp_revenue_report(?, ?)}")) {

            // Set the start and end date parameters for the stored procedure
            cs.setDate(1, Date.valueOf(start));
            cs.setDate(2, Date.valueOf(end));

            // Execute the stored procedure and get the results
            ResultSet rs = cs.executeQuery();

            // Loop through each row in the results
            while (rs.next()) {
                // Store each row as a Map (like a dictionary) with column names as keys
                Map<String, Object> row = new HashMap<>();
                row.put("roomType", rs.getString("room_type"));
                row.put("billsCount", rs.getInt("bills_count"));
                row.put("totalRevenue", rs.getDouble("total_revenue"));
                row.put("avgBill", rs.getDouble("avg_bill"));

                // Add this row to our results list
                results.add(row);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Revenue report error: " + e.getMessage());
        }

        // Return the report data
        return results;
    }
}
