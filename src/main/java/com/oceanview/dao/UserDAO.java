package com.oceanview.dao;

import com.oceanview.model.User;
import java.util.List;

/**
 * UserDAO - This is an interface for User data access.
 * An interface defines what methods the implementation class must have,
 * but does not contain the actual code for those methods.
 *
 * PATTERN: DAO (Data Access Object) - separates database code from business logic.
 * SOLID: Dependency Inversion - other classes depend on this interface, not the implementation.
 */
public interface UserDAO {

    // This method adds a new user to the database and returns true if successful
    boolean addUser(User user);

    // This method finds and returns a user by their unique ID number
    User getUserById(int id);

    // This method finds and returns a user by their username (used for login)
    User getUserByUsername(String username);

    // This method returns a list of all users stored in the database
    List<User> getAllUsers();

    // This method updates an existing user's details and returns true if successful
    boolean updateUser(User user);

    // This method deletes (deactivates) a user by their ID and returns true if successful
    boolean deleteUser(int id);
}
