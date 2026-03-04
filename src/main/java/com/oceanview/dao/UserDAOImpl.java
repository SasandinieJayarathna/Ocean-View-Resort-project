package com.oceanview.dao;

import com.oceanview.model.*;
import com.oceanview.util.DBConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAOImpl - This is the actual implementation of the UserDAO interface.
 * It contains all the SQL queries needed to work with the "users" table in the database.
 *
 * PATTERN: DAO (Data Access Object) - all user-related database code is kept in this one class.
 * SECURITY: We use PreparedStatement everywhere to prevent SQL injection attacks.
 */
public class UserDAOImpl implements UserDAO {

    // This stores our database connection so we can talk to the database
    private Connection connection;

    // Constructor that accepts an existing database connection (useful for testing)
    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // Default constructor - gets a database connection using our Singleton manager
    public UserDAOImpl() {
        try {
            // Get a database connection using our Singleton manager
            this.connection = DBConnectionManager.getInstance().getConnection();
        } catch (SQLException e) {
            // If we can't connect to the database, the app can't work, so we throw an error
            throw new RuntimeException("Failed to get DB connection", e);
        }
    }

    // This method inserts a new user into the database
    @Override
    public boolean addUser(User user) {
        // SQL query to insert a new user into the database
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role) VALUES (?,?,?,?,?)";

        // Using PreparedStatement to prevent SQL injection attacks
        // try-with-resources automatically closes the PreparedStatement when done
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Set each ? placeholder in the SQL with the user's actual data
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole());

            // Execute the INSERT and get how many rows were affected
            int rows = ps.executeUpdate();

            // If at least one row was inserted, it was successful
            if (rows > 0) {
                // Get the auto-generated ID that the database created for this new user
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    // Set the generated ID back into the user object so we can use it later
                    user.setUserId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error adding user: " + e.getMessage());
        }
        // If we get here, the insert failed
        return false;
    }

    // This method finds a user by their unique ID number
    @Override
    public User getUserById(int id) {
        // SQL query to get a user by their ID
        String sql = "SELECT * FROM users WHERE user_id = ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the ID we are looking for
            ps.setInt(1, id);

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // If we found a matching row, convert it to a User object and return it
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return null if no user was found with that ID
        return null;
    }

    // This method finds a user by their username (used during login)
    @Override
    public User getUserByUsername(String username) {
        // SQL query to get a user by their username
        String sql = "SELECT * FROM users WHERE username = ?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the username we are looking for
            ps.setString(1, username);

            // Execute the query and get the results
            ResultSet rs = ps.executeQuery();

            // If we found a matching row, convert it to a User object and return it
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return null if no user was found with that username
        return null;
    }

    // This method returns a list of all users from the database
    @Override
    public List<User> getAllUsers() {
        // Create an empty list to store the users we find
        List<User> list = new ArrayList<>();

        // SQL query to get all users, ordered by their ID
        String sql = "SELECT * FROM users ORDER BY user_id";

        // Using a simple Statement here since we don't have any parameters
        // try-with-resources automatically closes both the Statement and ResultSet
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            // Loop through each row in the results and add it to our list
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        // Return the list of users (could be empty if there are none)
        return list;
    }

    // This method updates an existing user's details in the database
    @Override
    public boolean updateUser(User user) {
        // SQL query to update a user's name, email, and active status
        String sql = "UPDATE users SET full_name=?, email=?, is_active=? WHERE user_id=?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set each ? placeholder with the updated values
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setBoolean(3, user.isActive());
            ps.setInt(4, user.getUserId());

            // Execute the UPDATE and return true if at least one row was changed
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    // This method "deletes" a user by setting them as inactive (soft delete)
    // We don't actually remove the row from the database - we just deactivate them
    @Override
    public boolean deleteUser(int id) {
        // SQL query to set a user as inactive instead of actually deleting them
        String sql = "UPDATE users SET is_active=FALSE WHERE user_id=?";

        // Using PreparedStatement to prevent SQL injection attacks
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            // Set the ? placeholder to the user ID we want to deactivate
            ps.setInt(1, id);

            // Execute the UPDATE and return true if at least one row was changed
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // If something goes wrong with the database, we catch the error here
            System.err.println("Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * This method converts a database row (ResultSet) into a Java User object.
     * It reads each column from the row and puts the values into the User object.
     * If the role is "ADMIN", we create an Admin object; otherwise we create a Staff object.
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        // Read the role column to decide what type of User to create
        String role = rs.getString("role");

        // If the role is ADMIN, create an Admin object; otherwise create a Staff object
        // This is using polymorphism - both Admin and Staff extend User
        User user = "ADMIN".equals(role) ? new Admin() : new Staff();

        // Set all the fields from the database row into the User object
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(role);
        user.setActive(rs.getBoolean("is_active"));

        // Only set the created_at field if it is not null in the database
        if (rs.getTimestamp("created_at") != null) {
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        }

        return user;
    }
}
