package com.oceanview.dao;

import com.oceanview.model.Room;
import com.oceanview.pattern.factory.RoomFactory;
import com.oceanview.util.DBConnectionManager;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * RoomDAOImpl - This is the actual implementation of the RoomDAO interface.
 * It contains all the SQL queries needed to work with the "rooms" table in the database.
 *
 * PATTERN: DAO (Data Access Object) - all room-related database code is kept in this one class.
 * USES: RoomFactory (Factory pattern) to create the correct Room subtype from database results.
 * FEATURE: getAvailableRooms() calls the stored procedure sp_get_available_rooms.
 */
public class RoomDAOImpl implements RoomDAO {

    // This stores our database connection so we can talk to the database
    private Connection connection;

    // Constructor that accepts an existing database connection (useful for testing)
    public RoomDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // Default constructor - gets a database connection using our Singleton manager
    public RoomDAOImpl() {
        try {
            // Get a database connection using our Singleton manager
            this.connection = DBConnectionManager.getInstance().getConnection();
        } catch (SQLException e) {
            // If we can't connect to the database, the app can't work, so we throw an error
            throw new RuntimeException("Failed to get DB connection", e);
        }
    }

    // This method inserts a new room into the database
    @Override
    public boolean addRoom(Room room) {
        // SQL query to insert a new room into the database
        String sql = "INSERT INTO rooms (room_number, room_type, price_per_night, description, max_occupancy) VALUES (?,?,?,?,?)";

        // Using PreparedStatement to prevent SQL injection attacks
        // try-with-resources automatically closes the PreparedStatement when done
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set each ? placeholder in the SQL with the room's actual data
            ps.setString(1, room.getRoomNumber());
            ps.setString(2, room.getRoomType());
            ps.setDouble(3, room.getPricePerNight());
            ps.setString(4, room.getDescription());
            ps.setInt(5, room.getMaxOccupancy());

            // Execute the INSERT and get how many rows were affected
            int rows = ps.executeUpdate();

            // If at least one row was inserted, it was successful
            if (rows > 0) {
                // Get the auto-generated ID that the database created for this new room
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    // Set the generated ID back into the room object so we can use it later
                    room.setRoomId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error adding room: " + e.getMessage());
        }
        // If we get here, the insert failed
        return false;
    }

    // This method finds a room by its unique ID number
    @Override
    public Room getRoomById(int id) {
        // SQL query to get a room by its ID
        String sql = "SELECT * FROM rooms WHERE room_id = ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the ID we are looking for
            ps.setInt(1, id);

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // If we found a matching row, convert it to a Room object and return it
            if (rs.next()) {
                return mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return null if no room was found with that ID
        return null;
    }

    // This method finds a room by its room number (e.g., "101", "202")
    @Override
    public Room getRoomByNumber(String number) {
        // SQL query to get a room by its room number
        String sql = "SELECT * FROM rooms WHERE room_number = ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the room number we are looking for
            ps.setString(1, number);

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // If we found a matching row, convert it to a Room object and return it
            if (rs.next()) {
                return mapResultSetToRoom(rs);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return null if no room was found with that number
        return null;
    }

    // This method returns a list of all rooms from the database
    @Override
    public List<Room> getAllRooms() {
        // Create an empty list to store the rooms we find
        List<Room> list = new ArrayList<>();

        // SQL query to get all rooms, ordered by room number
        String sql = "SELECT * FROM rooms ORDER BY room_number";

        // Using a simple Statement here since we don't have any parameters
        // try-with-resources automatically closes both the Statement and ResultSet
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            // Loop through each row in the results and add it to our list
            while (rs.next()) {
                list.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return the list of rooms (could be empty if there are none)
        return list;
    }

    /**
     * This method calls the stored procedure sp_get_available_rooms to find rooms
     * that are available between the given check-in and check-out dates.
     * A stored procedure is a pre-written SQL program stored in the database.
     * We use CallableStatement to call stored procedures from Java.
     */
    @Override
    public List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut, String type) {
        // Create an empty list to store the available rooms
        List<Room> list = new ArrayList<>();

        // Using CallableStatement to call the stored procedure in the database
        // The {CALL ...} syntax is how Java calls a stored procedure
        try (CallableStatement cs = connection.prepareCall("{CALL sp_get_available_rooms(?, ?, ?)}")) {
            // Set the check-in date parameter (convert Java LocalDate to SQL Date)
            cs.setDate(1, Date.valueOf(checkIn));
            // Set the check-out date parameter
            cs.setDate(2, Date.valueOf(checkOut));

            // Set the room type filter - if no type is specified, pass NULL to get all types
            if (type != null && !type.isEmpty()) {
                cs.setString(3, type);
            } else {
                cs.setNull(3, Types.VARCHAR);
            }

            // Execute the stored procedure and get the results
            ResultSet rs = cs.executeQuery();

            // Loop through each row and add the room to our list
            while (rs.next()) {
                list.add(mapResultSetToRoom(rs));
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error getting available rooms: " + e.getMessage());
        }
        // Return the list of available rooms
        return list;
    }

    // This method updates an existing room's details in the database
    @Override
    public boolean updateRoom(Room room) {
        // SQL query to update a room's price, availability, description, and max occupancy
        String sql = "UPDATE rooms SET price_per_night=?, is_available=?, description=?, max_occupancy=? WHERE room_id=?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set each ? placeholder with the updated values
            ps.setDouble(1, room.getPricePerNight());
            ps.setBoolean(2, room.isAvailable());
            ps.setString(3, room.getDescription());
            ps.setInt(4, room.getMaxOccupancy());
            ps.setInt(5, room.getRoomId());

            // Execute the UPDATE and return true if at least one row was changed
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * This method converts a database row (ResultSet) into a Java Room object.
     * It uses the RoomFactory to create the correct type of room (Standard, Deluxe, or Suite).
     * PATTERN: Factory - RoomFactory decides which Room subclass to create based on the room type.
     */
    private Room mapResultSetToRoom(ResultSet rs) throws SQLException {
        // Read the room type from the database row
        String roomType = rs.getString("room_type");

        // Use the Factory pattern to create the right type of Room object
        // e.g., "STANDARD" creates StandardRoom, "DELUXE" creates DeluxeRoom, etc.
        Room room = RoomFactory.createRoom(roomType);

        // Set all the fields from the database row into the Room object
        room.setRoomId(rs.getInt("room_id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setPricePerNight(rs.getDouble("price_per_night")); // Room Only rate
        room.setBbPrice(rs.getDouble("bb_price"));              // Bed & Breakfast rate
        room.setHbPrice(rs.getDouble("hb_price"));              // Half Board rate
        room.setFbPrice(rs.getDouble("fb_price"));              // Full Board rate
        room.setAvailable(rs.getBoolean("is_available"));
        room.setDescription(rs.getString("description"));
        room.setMaxOccupancy(rs.getInt("max_occupancy"));

        return room;
    }
}
