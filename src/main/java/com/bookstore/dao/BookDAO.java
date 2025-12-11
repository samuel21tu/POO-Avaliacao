package com.bookstore.dao;

import com.bookstore.model.Author;
import com.bookstore.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    private AuthorDAO authorDAO = new AuthorDAO();

    public void create(Book book) {
        String sql = "INSERT INTO books(title, price, author_id) VALUES(?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setDouble(2, book.getPrice());
            if (book.getAuthor() != null) {
                pstmt.setInt(3, book.getAuthor().getId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(Book book) {
        String sql = "UPDATE books SET title = ?, price = ?, author_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setDouble(2, book.getPrice());
            if (book.getAuthor() != null) {
                pstmt.setInt(3, book.getAuthor().getId());
            } else {
                pstmt.setNull(3, Types.INTEGER);
            }
            pstmt.setInt(4, book.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int authorId = rs.getInt("author_id");
                Author author = authorDAO.findById(authorId);
                
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("price"),
                        author
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return books;
    }

    public List<Book> findByTitle(String title) {
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        List<Book> books = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int authorId = rs.getInt("author_id");
                Author author = authorDAO.findById(authorId);

                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getDouble("price"),
                        author
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return books;
    }
}
