package dao;

import model.Book;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteBookDao implements BookDao {
    private final String URL = "jdbc:sqlite:src/main/resources/books.db";

    public void createBookTable() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                String sql = "CREATE TABLE IF NOT EXISTS books ("
                        + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + "title TEXT NOT NULL, "
                        + "author TEXT, "
                        + "price REAL, "
                        + "quantity INTEGER"
                        + ");";

                try (Statement stmt = conn.createStatement()) {
                    stmt.execute(sql);
                    System.out.println("ok");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertBook(String title, String author, double price, int quantity) {
        try (Connection conn = DriverManager.getConnection(URL)) {

            String sql = "INSERT INTO books(title, author, price, quantity) " +
                    "VALUES(?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, title);
                pstmt.setString(2, author);
                pstmt.setDouble(3, price);
                pstmt.setInt(4, quantity);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void insertBook(String filename) {
        List<String> books = parseBookCSV(filename);

        for (String book : books) {
            String[] data = book.split(",");
            String title = data[0];
            String author = data[1];
            double price = Double.parseDouble(data[2]);
            int quantity = Integer.parseInt(data[3]);

            insertBook(title, author, price, quantity);
        }
    }

    private List<String> parseBookCSV (String filename) {
        List<String> books = new ArrayList<>();

        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)) {

            String line;

            while ((line = br.readLine()) != null) {
                books.add(line);
            }

            books.remove(0); // header 제거

        } catch (FileNotFoundException e) {
            System.err.println(filename + " 파일을 찾을 수 없습니다.");
        } catch (IOException e) {
            System.err.println("파일 읽기 오류" + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }


    public void deleteBook(int id) {
        try (Connection conn = DriverManager.getConnection(URL)) {

            String sql = "DELETE FROM books WHERE id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void updateBookPrice(int id, double price) {
        try (Connection conn = DriverManager.getConnection(URL)) {

            String sql = "UPDATE books SET price = ? WHERE id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, price);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateQuantity(int id, int quantity) {
        try (Connection conn = DriverManager.getConnection(URL)) {

            String sql = "UPDATE books SET quantity = ? WHERE id = ?";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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