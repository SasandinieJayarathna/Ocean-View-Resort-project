package com.oceanview.service;

import com.oceanview.dao.UserDAO;
import com.oceanview.model.User;
import org.mindrot.jbcrypt.BCrypt;

/**
 * AuthService — Handles user authentication.
 * SOLID: Single Responsibility — only handles authentication.
 * SOLID: Dependency Inversion — depends on UserDAO interface, not implementation.
 */
public class AuthService {
    private final UserDAO userDAO;

    /** Constructor injection — enables testing with mock DAO. */
    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user by username and password.
     * @return User object if credentials valid, null otherwise.
     */
    public User authenticate(String username, String password) {
        if (username == null || password == null) return null;
        User user = userDAO.getUserByUsername(username.trim());
        if (user == null) return null;
        if (!user.isActive()) return null;
        if (BCrypt.checkpw(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }
}
