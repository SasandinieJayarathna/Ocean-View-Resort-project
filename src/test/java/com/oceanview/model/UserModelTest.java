package com.oceanview.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

/** Comprehensive tests for User, Staff, Admin model classes. */
class UserModelTest {
    @Test @DisplayName("TC-UM001: Staff constructor sets all fields")
    void staffConstructor() {
        Staff s = new Staff("staff1", "hash", "John Doe", "john@test.com");
        assertEquals("staff1", s.getUsername());
        assertEquals("hash", s.getPasswordHash());
        assertEquals("John Doe", s.getFullName());
        assertEquals("john@test.com", s.getEmail());
        assertEquals("STAFF", s.getRole());
        assertTrue(s.isActive());
    }

    @Test @DisplayName("TC-UM002: Admin constructor sets all fields")
    void adminConstructor() {
        Admin a = new Admin("admin1", "hash", "Admin User", "admin@test.com");
        assertEquals("admin1", a.getUsername());
        assertEquals("ADMIN", a.getRole());
        assertTrue(a.isActive());
    }

    @Test @DisplayName("TC-UM003: Staff default constructor sets role")
    void staffDefaultConstructor() {
        Staff s = new Staff();
        assertEquals("STAFF", s.getRole());
        assertTrue(s.isActive());
    }

    @Test @DisplayName("TC-UM004: Admin default constructor sets role")
    void adminDefaultConstructor() {
        Admin a = new Admin();
        assertEquals("ADMIN", a.getRole());
    }

    @Test @DisplayName("TC-UM005: User setters and getters work")
    void userSettersGetters() {
        Staff s = new Staff();
        s.setUserId(10);
        s.setUsername("test");
        s.setPasswordHash("newhash");
        s.setFullName("Test User");
        s.setEmail("test@test.com");
        s.setRole("STAFF");
        s.setActive(false);
        LocalDateTime now = LocalDateTime.now();
        s.setCreatedAt(now);

        assertEquals(10, s.getUserId());
        assertEquals("test", s.getUsername());
        assertEquals("newhash", s.getPasswordHash());
        assertEquals("Test User", s.getFullName());
        assertEquals("test@test.com", s.getEmail());
        assertEquals("STAFF", s.getRole());
        assertFalse(s.isActive());
        assertEquals(now, s.getCreatedAt());
    }

    @Test @DisplayName("TC-UM006: User toString contains class info")
    void userToString() {
        Staff s = new Staff("staff1", "hash", "John", "john@test.com");
        s.setUserId(1);
        String str = s.toString();
        assertTrue(str.contains("Staff"));
        assertTrue(str.contains("staff1"));
    }

    @Test @DisplayName("TC-UM007: Admin is instance of User")
    void adminIsUser() {
        Admin a = new Admin();
        assertTrue(a instanceof User);
    }
}
