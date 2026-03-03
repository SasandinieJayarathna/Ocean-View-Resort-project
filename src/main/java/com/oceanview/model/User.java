package com.oceanview.model;

import java.time.LocalDateTime;

/**
 * User — Abstract base class for system users.
 * DESIGN: Inheritance hierarchy (User → Staff, Admin).
 * SOLID: Liskov Substitution — subtypes substitute safely.
 * LAYER: Model (Domain)
 */
public abstract class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;

    public User() { this.isActive = true; }

    public User(String username, String passwordHash, String fullName, String email, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.isActive = true;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{userId=" + userId + ", username='" + username + "', role='" + role + "'}";
    }
}
