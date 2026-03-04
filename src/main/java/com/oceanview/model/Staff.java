package com.oceanview.model;

// ============================================================
// Staff.java
// This file defines the Staff class, which represents a
// front-desk employee at Ocean View Resort.
// It extends (inherits from) the User class, so it gets all
// the fields and methods from User automatically.
// The role is always set to "STAFF" for this type of user.
// ============================================================

/**
 * Staff — Front-desk employee. Inherits from User, role=STAFF.
 */
public class Staff extends User {

    // Default constructor — calls the parent (User) constructor
    // and then sets the role to "STAFF" so we know this user is a staff member.
    public Staff() {
        super();            // call the User default constructor
        setRole("STAFF");   // set the role to STAFF
    }

    // Parameterized constructor — creates a Staff member with all their details.
    // It passes the details up to the User constructor and sets the role to "STAFF".
    public Staff(String username, String passwordHash, String fullName, String email) {
        super(username, passwordHash, fullName, email, "STAFF");
    }
}
