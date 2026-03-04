package com.oceanview.model;

// ============================================================
// Admin.java
// This file defines the Admin class, which represents a
// manager with elevated privileges at Ocean View Resort.
// It extends (inherits from) the User class, just like Staff,
// but the role is set to "ADMIN" instead. Admins can do
// everything Staff can do, plus extra management tasks.
// ============================================================

/**
 * Admin — Manager with elevated privileges. Inherits from User, role=ADMIN.
 */
public class Admin extends User {

    // Default constructor — calls the parent (User) constructor
    // and then sets the role to "ADMIN" so we know this user is an admin.
    public Admin() {
        super();            // call the User default constructor
        setRole("ADMIN");   // set the role to ADMIN
    }

    // Parameterized constructor — creates an Admin with all their details.
    // It passes the details up to the User constructor and sets the role to "ADMIN".
    public Admin(String username, String passwordHash, String fullName, String email) {
        super(username, passwordHash, fullName, email, "ADMIN");
    }
}
