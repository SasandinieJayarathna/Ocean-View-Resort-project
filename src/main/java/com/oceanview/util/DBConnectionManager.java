package com.oceanview.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// The Singleton pattern means only ONE instance of this class can exist in the whole application
// This makes sense for a database connection manager because we only need one object
// to manage all our database connections - we don't want multiple managers fighting over resources
public class DBConnectionManager {

    // "volatile" makes sure that when one thread updates this variable, all other threads can see it
    // This is important because multiple threads might try to get the instance at the same time
    private static volatile DBConnectionManager instance;

    // These store the database connection details loaded from the properties file
    private String url, username, password;

    // The constructor is PRIVATE - this is the key to the Singleton pattern
    // Nobody outside this class can call "new DBConnectionManager()"
    // The only way to get an instance is through the getInstance() method below
    private DBConnectionManager() {
        try {
            // Properties is like a simple dictionary that reads key-value pairs from a file
            Properties props = new Properties();

            // Load the database settings from the db.properties file in our resources folder
            InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");

            // If the file doesn't exist, we can't connect to the database so we throw an error
            if (input == null) throw new RuntimeException("db.properties not found! Copy from db.properties.template");

            // Read all the properties from the file
            props.load(input);

            // Get each database setting from the properties file
            this.url = props.getProperty("db.url");           // The database URL (where to connect)
            this.username = props.getProperty("db.username");  // The database username
            this.password = props.getProperty("db.password");  // The database password

            // Load the JDBC driver class so Java knows how to talk to our specific database
            Class.forName(props.getProperty("db.driver"));
        } catch (Exception e) {
            // If anything goes wrong during setup, wrap it in a RuntimeException
            throw new RuntimeException("DB init failed: " + e.getMessage(), e);
        }
    }

    // This is how we get the single instance - this is called "double-checked locking"
    // First check: if instance already exists, return it right away (fast path)
    // Second check: inside synchronized block, check again in case another thread just created it
    // "synchronized" means only one thread can be inside this block at a time
    public static DBConnectionManager getInstance() {
        if (instance == null) {                                // First check (without locking)
            synchronized (DBConnectionManager.class) {         // Lock so only one thread enters
                if (instance == null) instance = new DBConnectionManager();  // Second check (with lock)
            }
        }
        return instance;  // Return the single instance
    }

    // Creates and returns a new database connection using the stored credentials
    // The caller is responsible for closing this connection when they are done with it
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // A helper method to safely close database resources like Connection, Statement, ResultSet
    // It takes multiple resources using varargs (...) and closes each one
    // If any resource is null or throws an error while closing, we just ignore it
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) { try { r.close(); } catch (Exception ignored) {} }
        }
    }
}
