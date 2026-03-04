package com.oceanview.service;

import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * AuthService - This class handles user authentication (login).
 * Its only job is to check if a username and password are correct.
 *
 * SOLID: Single Responsibility - this class only handles authentication, nothing else.
 * SOLID: Dependency Inversion - it depends on the UserDAO interface, not the implementation.
 */
public class AuthService {

    // This is the DAO we use to look up users from the database
    // It is marked 'final' so it cannot be changed after it is set
    private final UserDAO userDAO;

    // We pass in the DAO through the constructor - this is called Dependency Injection
    // It makes the class easier to test because we can pass in a fake (mock) DAO for testing
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * This method checks if the username and password are correct.
     * It returns the User object if login is successful, or null if it fails.
     */
    public User authenticate(String username, String password) {
        // First, check if the username or password is null (empty input)
        if (username == null || password == null) {
            return null;
        }

        // Try to find the user in the database by their username
        // .trim() removes any extra spaces from the beginning and end
        User user = userDAO.getUserByUsername(username.trim());

        // If no user was found with that username, login fails
        if (user == null) {
            return null;
        }

        // Check if the user account is active (not deactivated)
        if (!user.isActive()) {
            return null;
        }

        // We use BCrypt to compare the password - it's a secure hashing library
        // BCrypt.checkpw() compares the plain text password with the stored hash
        try {
            if (BCrypt.checkpw(password, user.getPasswordHash())) {
                // Password matches! Return the user object (login successful)
                return user;
            }
        } catch (IllegalArgumentException e) {
            // If something goes wrong with the BCrypt comparison, we log the error
            // This can happen if the stored hash is in an invalid format
            System.err.println("BCrypt error for user " + username + ": " + e.getMessage());
        }

        // If we get here, the password didn't match - login fails
        return null;
    }
}
