package com.oceanview.dao;

import com.oceanview.model.Bill;
import com.oceanview.util.DBConnectionManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * BillDAOImpl - This is the actual implementation of the BillDAO interface.
 * It contains all the SQL queries needed to work with the "bills" table in the database.
 *
 * PATTERN: DAO (Data Access Object) - all bill-related database code is kept in this one class.
 * SECURITY: We use PreparedStatement everywhere to prevent SQL injection attacks.
 */
public class BillDAOImpl implements BillDAO {

    // This stores our database connection so we can talk to the database
    private Connection connection;

    // Constructor that accepts an existing database connection (useful for testing)
    public BillDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // Default constructor - gets a database connection using our Singleton manager
    public BillDAOImpl() {
        try {
            // Get a database connection using our Singleton manager
            this.connection = DBConnectionManager.getInstance().getConnection();
        } catch (SQLException e) {
            // If we can't connect to the database, the app can't work, so we throw an error
            throw new RuntimeException("Failed to get DB connection", e);
        }
    }

    // This method saves a new bill to the database
    @Override
    public boolean saveBill(Bill bill) {
        // SQL query to insert a new bill with all its financial details
        String sql = "INSERT INTO bills (reservation_id, number_of_nights, room_rate, subtotal, tax_rate, tax_amount, discount_percent, discount_amount, total_amount, billing_strategy, generated_by) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        // Using PreparedStatement to prevent SQL injection attacks
        // try-with-resources automatically closes the PreparedStatement when done
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set each ? placeholder in the SQL with the bill's actual data
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

            // Execute the INSERT and get how many rows were affected
            int rows = ps.executeUpdate();

            // If at least one row was inserted, it was successful
            if (rows > 0) {
                // Get the auto-generated ID that the database created for this bill
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    // Set the generated ID back into the bill object so we can use it later
                    bill.setBillId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error saving bill: " + e.getMessage());
        }
        // If we get here, the insert failed
        return false;
    }

    // This method finds a bill by its associated reservation ID
    @Override
    public Bill getBillByReservationId(int reservationId) {
        // SQL query to get a bill by the reservation it belongs to
        String sql = "SELECT * FROM bills WHERE reservation_id = ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the reservation ID we are looking for
            ps.setInt(1, reservationId);

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // If we found a matching row, convert it to a Bill object and return it
            if (rs.next()) {
                return mapResultSetToBill(rs);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return null if no bill was found for that reservation
        return null;
    }

    // This method returns a list of all bills from the database
    @Override
    public List<Bill> getAllBills() {
        // Create an empty list to store the bills we find
        List<Bill> list = new ArrayList<>();

        // SQL query to get all bills, newest first
        String sql = "SELECT * FROM bills ORDER BY generated_at DESC";

        // Using a simple Statement here since we don't have any parameters
        // try-with-resources automatically closes both the Statement and ResultSet
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            // Loop through each row in the results and add it to our list
            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return the list of bills (could be empty if there are none)
        return list;
    }

    // This method returns bills that were generated within a given date range
    @Override
    public List<Bill> getBillsByDateRange(LocalDate start, LocalDate end) {
        // Create an empty list to store the bills we find
        List<Bill> list = new ArrayList<>();

        // SQL query to find bills generated between the start and end dates
        String sql = "SELECT * FROM bills WHERE generated_at BETWEEN ? AND ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Convert Java LocalDate to SQL Date for the database
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // Loop through each matching row and add it to our list
            while (rs.next()) {
                list.add(mapResultSetToBill(rs));
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return the list of bills in that date range
        return list;
    }

    /**
     * This method converts a database row (ResultSet) into a Java Bill object.
     * It reads each column from the row and puts the values into the Bill object.
     */
    private Bill mapResultSetToBill(ResultSet rs) throws SQLException {
        // Create a new empty Bill object
        Bill bill = new Bill();

        // Set all the fields from the database row into the Bill object
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

        // Only set the generated_at field if it is not null in the database
        if (rs.getTimestamp("generated_at") != null) {
            bill.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        }

        bill.setGeneratedBy(rs.getInt("generated_by"));

        return bill;
    }
}
