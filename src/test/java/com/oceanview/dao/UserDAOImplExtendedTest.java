package com.oceanview.dao;

import com.oceanview.model.Admin;
import com.oceanview.model.Staff;
import com.oceanview.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserDAOImplExtendedTest — Additional tests for UserDAOImpl covering
 * getUserById, getAllUsers, updateUser, deleteUser, and Admin mapping.
 */
@ExtendWith(MockitoExtension.class)
class UserDAOImplExtendedTest {
    @Mock private Connection connection;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;
    @Mock private Statement statement;

    private UserDAOImpl userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        userDAO = new UserDAOImpl(connection);
        lenient().when(connection.prepareStatement(any(String.class))).thenReturn(ps);
        lenient().when(connection.createStatement()).thenReturn(statement);
    }

    private void mockUserRow(String role) throws SQLException {
        when(rs.getInt("user_id")).thenReturn(1);
        when(rs.getString("username")).thenReturn("user1");
        when(rs.getString("password_hash")).thenReturn("$2a$10$hash");
        when(rs.getString("full_name")).thenReturn("Test User");
        when(rs.getString("email")).thenReturn("user@test.com");
        when(rs.getString("role")).thenReturn(role);
        when(rs.getBoolean("is_active")).thenReturn(true);
        when(rs.getTimestamp("created_at")).thenReturn(null);
    }

    @Test @DisplayName("TC-UE001: getUserById returns user when found")
    void getUserByIdFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockUserRow("STAFF");

        User user = userDAO.getUserById(1);
        assertNotNull(user);
        assertTrue(user instanceof Staff);
        assertEquals("user1", user.getUsername());
    }

    @Test @DisplayName("TC-UE002: getUserById returns null when not found")
    void getUserByIdNotFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertNull(userDAO.getUserById(999));
    }

    @Test @DisplayName("TC-UE003: getUserById maps ADMIN role to Admin class")
    void getUserByIdAdmin() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        mockUserRow("ADMIN");

        User user = userDAO.getUserById(1);
        assertNotNull(user);
        assertTrue(user instanceof Admin);
        assertEquals("ADMIN", user.getRole());
    }

    @Test @DisplayName("TC-UE004: getAllUsers returns list of users")
    void getAllUsers() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        mockUserRow("STAFF");

        List<User> users = userDAO.getAllUsers();
        assertEquals(1, users.size());
        assertEquals("user1", users.get(0).getUsername());
    }

    @Test @DisplayName("TC-UE005: getAllUsers returns empty list when none")
    void getAllUsersEmpty() throws SQLException {
        when(statement.executeQuery(any(String.class))).thenReturn(rs);
        when(rs.next()).thenReturn(false);
        assertTrue(userDAO.getAllUsers().isEmpty());
    }

    @Test @DisplayName("TC-UE006: updateUser returns true on success")
    void updateUserSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        Staff staff = new Staff("user1", "hash", "Updated Name", "new@test.com");
        staff.setUserId(1);
        assertTrue(userDAO.updateUser(staff));
    }

    @Test @DisplayName("TC-UE007: updateUser returns false on failure")
    void updateUserFailure() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        Staff staff = new Staff("user1", "hash", "Name", "test@test.com");
        staff.setUserId(999);
        assertFalse(userDAO.updateUser(staff));
    }

    @Test @DisplayName("TC-UE008: deleteUser returns true on success")
    void deleteUserSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        assertTrue(userDAO.deleteUser(1));
    }

    @Test @DisplayName("TC-UE009: deleteUser returns false on failure")
    void deleteUserFailure() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);
        assertFalse(userDAO.deleteUser(999));
    }

    @Test @DisplayName("TC-UE010: getUserById handles SQL exception")
    void getUserByIdSQLException() throws SQLException {
        when(ps.executeQuery()).thenThrow(new SQLException("DB error"));
        assertNull(userDAO.getUserById(1));
    }

    @Test @DisplayName("TC-UE011: User with created_at timestamp is mapped")
    void userWithTimestamp() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("user_id")).thenReturn(2);
        when(rs.getString("username")).thenReturn("admin1");
        when(rs.getString("password_hash")).thenReturn("$2a$10$hash2");
        when(rs.getString("full_name")).thenReturn("Admin User");
        when(rs.getString("email")).thenReturn("admin@test.com");
        when(rs.getString("role")).thenReturn("ADMIN");
        when(rs.getBoolean("is_active")).thenReturn(true);
        Timestamp ts = Timestamp.valueOf("2025-01-15 09:00:00");
        when(rs.getTimestamp("created_at")).thenReturn(ts);

        User user = userDAO.getUserById(2);
        assertNotNull(user);
        assertNotNull(user.getCreatedAt());
    }

    @Test @DisplayName("TC-UE012: updateUser handles SQL exception")
    void updateUserSQLException() throws SQLException {
        when(ps.executeUpdate()).thenThrow(new SQLException("DB error"));
        Staff staff = new Staff("user1", "hash", "Name", "test@test.com");
        staff.setUserId(1);
        assertFalse(userDAO.updateUser(staff));
    }

    @Test @DisplayName("TC-UE013: deleteUser handles SQL exception")
    void deleteUserSQLException() throws SQLException {
        when(ps.executeUpdate()).thenThrow(new SQLException("DB error"));
        assertFalse(userDAO.deleteUser(1));
    }
}
