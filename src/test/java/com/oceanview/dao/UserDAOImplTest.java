package com.oceanview.dao;

import com.oceanview.model.Staff;
import com.oceanview.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

/**
 * UserDAOImplTest — Mockito-based tests for UserDAOImpl.
 * Mocks JDBC Connection, PreparedStatement, and ResultSet.
 */
@ExtendWith(MockitoExtension.class)
class UserDAOImplTest {
    @Mock private Connection connection;
    @Mock private PreparedStatement ps;
    @Mock private ResultSet rs;
    @Mock private ResultSet generatedKeys;

    private UserDAOImpl userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        userDAO = new UserDAOImpl(connection);
        lenient().when(connection.prepareStatement(any(String.class))).thenReturn(ps);
        lenient().when(connection.prepareStatement(any(String.class), anyInt())).thenReturn(ps);
    }

    @Test @DisplayName("TC-D001: getUserByUsername returns user when found")
    void getUserByUsernameFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt("user_id")).thenReturn(1);
        when(rs.getString("username")).thenReturn("staff1");
        when(rs.getString("password_hash")).thenReturn("$2a$10$hash");
        when(rs.getString("full_name")).thenReturn("Test Staff");
        when(rs.getString("email")).thenReturn("staff@test.com");
        when(rs.getString("role")).thenReturn("STAFF");
        when(rs.getBoolean("is_active")).thenReturn(true);
        when(rs.getTimestamp("created_at")).thenReturn(null);

        User user = userDAO.getUserByUsername("staff1");
        assertNotNull(user);
        assertEquals("staff1", user.getUsername());
        assertEquals("STAFF", user.getRole());
    }

    @Test @DisplayName("TC-D002: getUserByUsername returns null when not found")
    void getUserByUsernameNotFound() throws SQLException {
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        User user = userDAO.getUserByUsername("unknown");
        assertNull(user);
    }

    @Test @DisplayName("TC-D003: addUser returns true on success")
    void addUserSuccess() throws SQLException {
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(generatedKeys);
        when(generatedKeys.next()).thenReturn(true);
        when(generatedKeys.getInt(1)).thenReturn(5);

        Staff staff = new Staff("newstaff", "hash", "New Staff", "new@test.com");
        assertTrue(userDAO.addUser(staff));
        assertEquals(5, staff.getUserId());
    }

    @Test @DisplayName("TC-D004: addUser returns false on failure")
    void addUserFailure() throws SQLException {
        when(ps.executeUpdate()).thenReturn(0);

        Staff staff = new Staff("fail", "hash", "Fail", "fail@test.com");
        assertFalse(userDAO.addUser(staff));
    }
}
