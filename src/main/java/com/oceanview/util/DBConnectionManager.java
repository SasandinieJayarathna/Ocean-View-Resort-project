package com.oceanview.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * DBConnectionManager — Singleton pattern for database connections.
 * PATTERN: Singleton (Creational) — private constructor, static getInstance().
 * THREAD SAFETY: Double-checked locking with volatile keyword.
 * SOLID: Single Responsibility — only manages DB connections.
 */
public class DBConnectionManager {
    private static volatile DBConnectionManager instance;
    private String url, username, password;

    /** PRIVATE constructor — key to Singleton pattern. Cannot be called externally. */
    private DBConnectionManager() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties");
            if (input == null) throw new RuntimeException("db.properties not found! Copy from db.properties.template");
            props.load(input);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            Class.forName(props.getProperty("db.driver"));
        } catch (Exception e) {
            throw new RuntimeException("DB init failed: " + e.getMessage(), e);
        }
    }

    /** Returns the single instance. Double-checked locking for thread safety. */
    public static DBConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DBConnectionManager.class) {
                if (instance == null) instance = new DBConnectionManager();
            }
        }
        return instance;
    }

    /** Gets a fresh JDBC connection. Caller MUST close it when done. */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /** Safely close resources (handles null). */
    public static void closeQuietly(AutoCloseable... resources) {
        for (AutoCloseable r : resources) {
            if (r != null) { try { r.close(); } catch (Exception ignored) {} }
        }
    }
}
