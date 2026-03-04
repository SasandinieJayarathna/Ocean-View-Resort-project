package com.oceanview.service;

import com.oceanview.dao.UserDAO;
import com.oceanview.model.Staff;
import com.oceanview.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AuthServiceTest — Tests for AuthService using Mockito.
 * Mocks UserDAO to test authentication logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock private UserDAO userDAO;
    private AuthService authService;

    @BeforeEach
    void setUp() { authService = new AuthService(userDAO); }

    private User createTestUser(String username, String password, boolean active) {
        Staff user = new Staff(username, BCrypt.hashpw(password, BCrypt.gensalt()), "Test User", "test@test.com");
        user.setUserId(1);
        user.setActive(active);
        return user;
    }

    @Test @DisplayName("TC-A001: Valid credentials returns user")
    void validCredentials() {
        User user = createTestUser("staff1", "password123", true);
        when(userDAO.getUserByUsername("staff1")).thenReturn(user);

        User result = authService.authenticate("staff1", "password123");
        assertNotNull(result);
        assertEquals("staff1", result.getUsername());
    }

    @Test @DisplayName("TC-A002: Invalid password returns null")
    void invalidPassword() {
        User user = createTestUser("staff1", "password123", true);
        when(userDAO.getUserByUsername("staff1")).thenReturn(user);

        assertNull(authService.authenticate("staff1", "wrongpassword"));
    }

    @Test @DisplayName("TC-A003: Unknown username returns null")
    void unknownUsername() {
        when(userDAO.getUserByUsername("unknown")).thenReturn(null);
        assertNull(authService.authenticate("unknown", "password123"));
    }

    @Test @DisplayName("TC-A004: Null username returns null")
    void nullUsername() { assertNull(authService.authenticate(null, "password123")); }

    @Test @DisplayName("TC-A005: Null password returns null")
    void nullPassword() { assertNull(authService.authenticate("staff1", null)); }

    @Test @DisplayName("TC-A006: Inactive user returns null")
    void inactiveUser() {
        User user = createTestUser("staff1", "password123", false);
        when(userDAO.getUserByUsername("staff1")).thenReturn(user);

        assertNull(authService.authenticate("staff1", "password123"));
    }
}
