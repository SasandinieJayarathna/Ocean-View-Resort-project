package com.oceanview.model;

/** Admin — Manager with elevated privileges. Inherits from User, role=ADMIN. */
public class Admin extends User {
    public Admin() { super(); setRole("ADMIN"); }
    public Admin(String username, String passwordHash, String fullName, String email) {
        super(username, passwordHash, fullName, email, "ADMIN");
    }
}
