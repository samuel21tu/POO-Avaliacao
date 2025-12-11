package com.bookstore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // PostgreSQL Connection
    private static final String URL = "jdbc:postgresql://localhost:5432/bookstore";
    private static final String USER = "bookstore_user";
    private static final String PASSWORD = "password";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create Authors table (using SERIAL for auto-increment in Postgres)
            String createAuthors = "CREATE TABLE IF NOT EXISTS authors (" +
                    "id SERIAL PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "nationality VARCHAR(50)" +
                    ");";
            stmt.execute(createAuthors);

            // Create Books table
            String createBooks = "CREATE TABLE IF NOT EXISTS books (" +
                    "id SERIAL PRIMARY KEY," +
                    "title VARCHAR(200) NOT NULL," +
                    "price DECIMAL(10,2)," +
                    "author_id INTEGER," +
                    "FOREIGN KEY (author_id) REFERENCES authors(id)" +
                    ");";
            stmt.execute(createBooks);

            System.out.println("Database initialized (PostgreSQL).");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
