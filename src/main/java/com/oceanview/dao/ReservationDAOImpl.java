package com.oceanview.dao;

import com.oceanview.model.Reservation;
import com.oceanview.util.DBConnectionManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ReservationDAOImpl - This is the actual implementation of the ReservationDAO interface.
 * It contains all the SQL queries needed to work with the "reservations" table in the database.
 *
 * PATTERN: DAO (Data Access Object) - all reservation-related database code is in this one class.
 * SOLID: Single Responsibility - this class only handles reservation database operations.
 */
public class ReservationDAOImpl implements ReservationDAO {

    // This stores our database connection so we can talk to the database
    private Connection connection;

    // Constructor that accepts an existing database connection (useful for testing)
    public ReservationDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // Default constructor - gets a database connection using our Singleton manager
    public ReservationDAOImpl() {
        try {
            // Get a database connection using our Singleton manager
            this.connection = DBConnectionManager.getInstance().getConnection();
        } catch (SQLException e) {
            // If we can't connect to the database, the app can't work, so we throw an error
            throw new RuntimeException("Failed to get DB connection", e);
        }
    }

    // This method inserts a new reservation into the database
    @Override
    public boolean addReservation(Reservation r) {
        // SQL query to insert a new reservation with all its details
        String sql = "INSERT INTO reservations (reservation_number, guest_name, guest_address, contact_number, guest_email, room_id, room_type, check_in_date, check_out_date, status, special_requests, created_by) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        // Using PreparedStatement to prevent SQL injection attacks
        // try-with-resources automatically closes the PreparedStatement when done
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set each ? placeholder in the SQL with the reservation's actual data
            ps.setString(1, r.getReservationNumber());
            ps.setString(2, r.getGuestName());
            ps.setString(3, r.getGuestAddress());
            ps.setString(4, r.getContactNumber());
            ps.setString(5, r.getGuestEmail());
            ps.setInt(6, r.getRoomId());
            ps.setString(7, r.getRoomType());
            // Convert Java LocalDate to SQL Date for the database
            ps.setDate(8, Date.valueOf(r.getCheckInDate()));
            ps.setDate(9, Date.valueOf(r.getCheckOutDate()));
            ps.setString(10, r.getStatus());
            ps.setString(11, r.getSpecialRequests());
            ps.setInt(12, r.getCreatedBy());

            // Execute the INSERT and get how many rows were affected
            int rows = ps.executeUpdate();

            // If at least one row was inserted, it was successful
            if (rows > 0) {
                // Get the auto-generated ID that the database created for this reservation
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    // Set the generated ID back into the reservation object
                    r.setReservationId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error adding reservation: " + e.getMessage());
        }
        // If we get here, the insert failed
        return false;
    }

    // This method finds a reservation by its unique ID number
    @Override
    public Reservation getReservationById(int id) {
        // SQL query to get a reservation by its ID
        String sql = "SELECT * FROM reservations WHERE reservation_id = ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the ID we are looking for
            ps.setInt(1, id);

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // If we found a matching row, convert it to a Reservation object and return it
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return null if no reservation was found with that ID
        return null;
    }

    // This method finds a reservation by its reservation number (e.g., "RES-0001")
    @Override
    public Reservation getReservationByNumber(String number) {
        // SQL query to get a reservation by its reservation number
        String sql = "SELECT * FROM reservations WHERE reservation_number = ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the reservation number we are looking for
            ps.setString(1, number);

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // If we found a matching row, convert it to a Reservation object and return it
            if (rs.next()) {
                return mapRow(rs);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return null if no reservation was found with that number
        return null;
    }

    // This method returns a list of all reservations from the database
    @Override
    public List<Reservation> getAllReservations() {
        // Create an empty list to store the reservations we find
        List<Reservation> list = new ArrayList<>();

        // SQL query to get all reservations, newest first
        String sql = "SELECT * FROM reservations ORDER BY created_at DESC";

        // Using a simple Statement here since we don't have any parameters
        // try-with-resources automatically closes both the Statement and ResultSet
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            // Loop through each row in the results and add it to our list
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return the list of reservations (could be empty if there are none)
        return list;
    }

    // This method searches reservations by a keyword (matches guest name, reservation number, or phone)
    @Override
    public List<Reservation> searchReservations(String keyword) {
        // Create an empty list to store the matching reservations
        List<Reservation> list = new ArrayList<>();

        // SQL query to search across multiple columns using LIKE (partial matching)
        String sql = "SELECT * FROM reservations WHERE guest_name LIKE ? OR reservation_number LIKE ? OR contact_number LIKE ? ORDER BY created_at DESC";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Add % wildcard characters around the keyword for partial matching
            // e.g., "%John%" will match "John Smith", "Johnny", etc.
            String p = "%" + keyword + "%";
            ps.setString(1, p);
            ps.setString(2, p);
            ps.setString(3, p);

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // Loop through each matching row and add it to our list
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return the list of matching reservations
        return list;
    }

    // This method returns reservations that fall within a given date range
    @Override
    public List<Reservation> getReservationsByDateRange(LocalDate start, LocalDate end) {
        // Create an empty list to store the reservations we find
        List<Reservation> list = new ArrayList<>();

        // SQL query to find reservations within the given date range
        String sql = "SELECT * FROM reservations WHERE check_in_date >= ? AND check_out_date <= ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Convert Java LocalDate to SQL Date for the database
            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // Loop through each matching row and add it to our list
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return the list of reservations in that date range
        return list;
    }

    // This method updates an existing reservation's details in the database
    @Override
    public boolean updateReservation(Reservation r) {
        // SQL query to update guest details, status, and special requests
        String sql = "UPDATE reservations SET guest_name=?, guest_address=?, contact_number=?, guest_email=?, status=?, special_requests=? WHERE reservation_id=?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set each ? placeholder with the updated values
            ps.setString(1, r.getGuestName());
            ps.setString(2, r.getGuestAddress());
            ps.setString(3, r.getContactNumber());
            ps.setString(4, r.getGuestEmail());
            ps.setString(5, r.getStatus());
            ps.setString(6, r.getSpecialRequests());
            ps.setInt(7, r.getReservationId());

            // Execute the UPDATE and return true if at least one row was changed
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    // This method cancels a reservation by changing its status to "CANCELLED"
    // It's a soft delete - we don't actually remove the row from the database
    @Override
    public boolean deleteReservation(int id) {
        // SQL query to set the reservation status to CANCELLED instead of actually deleting it
        String sql = "UPDATE reservations SET status='CANCELLED' WHERE reservation_id=?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the reservation ID we want to cancel
            ps.setInt(1, id);

            // Execute the UPDATE and return true if at least one row was changed
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    // This method counts how many reservations were created today
    @Override
    public int getTodayReservationCount() {
        // SQL query to count reservations where the creation date is today
        // CURDATE() is a MySQL function that returns today's date
        String sql = "SELECT COUNT(*) FROM reservations WHERE DATE(created_at) = CURDATE()";

        // Using a simple Statement since there are no parameters
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            // Get the count from the first (and only) row of results
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return 0 if something went wrong
        return 0;
    }

    /**
     * This method converts a database row (ResultSet) into a Java Reservation object.
     * It reads each column from the row and puts the values into the Reservation object.
     */
    private Reservation mapRow(ResultSet rs) throws SQLException {
        // Create a new empty Reservation object
        Reservation r = new Reservation();

        // Set all the fields from the database row into the Reservation object
        r.setReservationId(rs.getInt("reservation_id"));
        r.setReservationNumber(rs.getString("reservation_number"));
        r.setGuestName(rs.getString("guest_name"));
        r.setGuestAddress(rs.getString("guest_address"));
        r.setContactNumber(rs.getString("contact_number"));
        r.setGuestEmail(rs.getString("guest_email"));
        r.setRoomId(rs.getInt("room_id"));
        r.setRoomType(rs.getString("room_type"));

        // Convert SQL Date to Java LocalDate for check-in and check-out dates
        r.setCheckInDate(rs.getDate("check_in_date").toLocalDate());
        r.setCheckOutDate(rs.getDate("check_out_date").toLocalDate());

        r.setStatus(rs.getString("status"));
        r.setSpecialRequests(rs.getString("special_requests"));
        r.setCreatedBy(rs.getInt("created_by"));

        // Only set created_at if it is not null in the database
        if (rs.getTimestamp("created_at") != null) {
            r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return r;
    }
}
