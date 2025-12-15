package com.bookstore.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    // SQLite Connection
    private static final String URL = "jdbc:sqlite:bookstore.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initialize() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Create Authors table (using INTEGER PRIMARY KEY AUTOINCREMENT for SQLite)
            String createAuthors = "CREATE TABLE IF NOT EXISTS authors (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name VARCHAR(100) NOT NULL," +
                    "nationality VARCHAR(50)" +
                    ");";
            stmt.execute(createAuthors);

            // Create Books table
            String createBooks = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title VARCHAR(200) NOT NULL," +
                    "price DECIMAL(10,2)," +
                    "author_id INTEGER," +
                    "FOREIGN KEY (author_id) REFERENCES authors(id)" +
                    ");";
            stmt.execute(createBooks);

            System.out.println("Database initialized (SQLite).");

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }
}
