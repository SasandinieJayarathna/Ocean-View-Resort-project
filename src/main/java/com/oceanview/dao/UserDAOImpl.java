package com.oceanview.dao;

import com.oceanview.model.*;
import com.oceanview.util.DBConnectionManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAOImpl — JDBC implementation of UserDAO.
 * PATTERN: DAO — isolates all user-related SQL.
 * SECURITY: Uses PreparedStatement to prevent SQL injection.
 */
public class UserDAOImpl implements UserDAO {
    private Connection connection;

    public UserDAOImpl(Connection connection) { this.connection = connection; }

    public UserDAOImpl() {
        try { this.connection = DBConnectionManager.getInstance().getConnection(); }
        catch (SQLException e) { throw new RuntimeException("Failed to get DB connection", e); }
    }

    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, role) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getFullName());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getRole());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) user.setUserId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) { System.err.println("Error adding user: " + e.getMessage()); }
        return false;
    }

    @Override
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToUser(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToUser(rs);
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id";
        try (Statement st = connection.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToUser(rs));
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return list;
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET full_name=?, email=?, is_active=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setBoolean(3, user.isActive());
            ps.setInt(4, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return false;
    }

    @Override
    public boolean deleteUser(int id) {
        String sql = "UPDATE users SET is_active=FALSE WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { System.err.println("Error: " + e.getMessage()); }
        return false;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user = "ADMIN".equals(role) ? new Admin() : new Staff();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(role);
        user.setActive(rs.getBoolean("is_active"));
        if (rs.getTimestamp("created_at") != null)
            user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return user;
    }
}
