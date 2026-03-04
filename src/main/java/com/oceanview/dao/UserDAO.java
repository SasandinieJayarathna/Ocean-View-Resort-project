package com.oceanview.dao;

import com.oceanview.model.User;
import java.util.List;

/**
 * UserDAO — Data access interface for User operations.
 * PATTERN: DAO (Data Access Object) — decouples business logic from database.
 * SOLID: Interface Segregation — focused only on User operations.
 * SOLID: Dependency Inversion — services depend on this interface, not implementation.
 */
public interface UserDAO {
    boolean addUser(User user);
    User getUserById(int id);
    User getUserByUsername(String username);
    List<User> getAllUsers();
    boolean updateUser(User user);
    boolean deleteUser(int id);
}
