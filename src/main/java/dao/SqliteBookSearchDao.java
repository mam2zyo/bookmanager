package dao;

import model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteBookSearchDao implements BookSearchDao {

    private final String URL = "jdbc:sqlite:src/main/resources/books.db";

    public Book findById(int id) {
        Book book = null;
        try (Connection conn = DriverManager.getConnection(URL)) {
            String sql = "SELECT * FROM books WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.executeQuery(sql);

            if (!rs.next()) {
                return null;
            }

            do {
                book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
            } while (rs.next());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }


    public List<Book> findByTitleOrAuthor(String substring) {

        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL)) {

            String sql = "SELECT * FROM books" +
                    " WHERE title LIKE ? COLLATE NOCASE" +
                    " OR author LIKE ? COLLATE NOCASE;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, substring);
            pstmt.setString(2, substring);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.executeQuery(sql);

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> findByPriceRange(double min, double max) {

        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL)) {

            String sql = "SELECT * FROM books WHERE price BETWEEN ? AND ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, min);
            pstmt.setDouble(2, max);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.executeQuery(sql);

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> findLowStock(int threshold) {

        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL)) {

            String sql = "SELECT * FROM books WHERE quantity <= ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, threshold);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.executeQuery(sql);

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL)) {

            String sql = "SELECT * FROM books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}