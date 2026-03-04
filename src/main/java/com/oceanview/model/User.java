package com.oceanview.model;

import java.time.LocalDateTime;

// ============================================================
// User.java
// This file defines the User class, which is the parent class
// for all types of users in the Ocean View hotel system.
// It is abstract, meaning we can't create a User object directly —
// we have to create a Staff or Admin instead.
// It uses inheritance: Staff and Admin extend this class.
// ============================================================

/**
 * User — Abstract base class for system users.
 * DESIGN: Inheritance hierarchy (User -> Staff, Admin).
 * SOLID: Liskov Substitution — subtypes substitute safely.
 * LAYER: Model (Domain)
 */
public abstract class User {

    // These are the private fields that store user information.
    // They are private so that other classes can't access them directly —
    // instead, they have to use the getter and setter methods below.
    private int userId;              // unique ID for each user in the database
    private String username;         // the login name the user types in
    private String passwordHash;     // the hashed version of the password (we never store plain text passwords!)
    private String fullName;         // the user's real full name
    private String email;            // the user's email address
    private String role;             // what type of user they are, e.g. "STAFF" or "ADMIN"
    private boolean isActive;        // whether this account is currently active or disabled
    private LocalDateTime createdAt; // the date and time the account was created

    // Default constructor — this runs when we create a user without passing any details.
    // It just sets the account to active by default.
    public User() {
        this.isActive = true;
    }

    // Parameterized constructor — this lets us create a user and set their details all at once.
    // We pass in the username, password hash, full name, email, and role.
    public User(String username, String passwordHash, String fullName, String email, String role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
        this.isActive = true; // new accounts are active by default
    }

    // =====================================================
    // Getters and setters
    // These let other classes read and change the private
    // fields above. This is called "encapsulation" — we
    // control how the data is accessed and modified.
    // =====================================================

    // getter for userId — returns the user's unique ID
    public int getUserId() {
        return userId;
    }

    // setter for userId — lets us assign a unique ID to this user
    public void setUserId(int userId) {
        this.userId = userId;
    }

    // getter for username — returns the login name
    public String getUsername() {
        return username;
    }

    // setter for username — lets us change the login name
    public void setUsername(String username) {
        this.username = username;
    }

    // getter for passwordHash — returns the hashed password
    public String getPasswordHash() {
        return passwordHash;
    }

    // setter for passwordHash — lets us update the hashed password
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // getter for fullName — returns the user's real name
    public String getFullName() {
        return fullName;
    }

    // setter for fullName — lets us change the user's real name
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    // getter for email — returns the email address
    public String getEmail() {
        return email;
    }

    // setter for email — lets us update the email address
    public void setEmail(String email) {
        this.email = email;
    }

    // getter for role — returns the user's role (e.g. "STAFF" or "ADMIN")
    public String getRole() {
        return role;
    }

    // setter for role — lets us change the user's role
    public void setRole(String role) {
        this.role = role;
    }

    // getter for isActive — returns true if the account is active
    public boolean isActive() {
        return isActive;
    }

    // setter for isActive — lets us activate or deactivate the account
    public void setActive(boolean active) {
        isActive = active;
    }

    // getter for createdAt — returns the date/time the account was created
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // setter for createdAt — lets us set the creation timestamp
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // toString method — this gives us a readable string when we print a User object.
    // It shows the class name (Staff or Admin), the user ID, username, and role.
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{userId=" + userId + ", username='" + username + "', role='" + role + "'}";
    }
}
