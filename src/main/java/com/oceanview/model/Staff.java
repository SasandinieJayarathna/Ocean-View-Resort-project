package com.oceanview.model;

/** Staff — Front-desk employee. Inherits from User, role=STAFF. */
public class Staff extends User {
    public Staff() { super(); setRole("STAFF"); }
    public Staff(String username, String passwordHash, String fullName, String email) {
        super(username, passwordHash, fullName, email, "STAFF");
    }
}
