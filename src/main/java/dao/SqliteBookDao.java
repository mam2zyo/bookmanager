package dao;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import model.Book;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteBookDao implements BookDao {

    private final String URL = "jdbc:sqlite:src/main/resources/books.db";
    private final String LOG_URL = "jdbc:sqlite:src/main/resources/books.log";

    @Override
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
    public boolean insertBook(String title, String author, double price, int quantity) {
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

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public void insertBooks(String filename) {

        List<Book> books = parseBookCSV(filename);

        for (Book book : books) {
            String title = book.getTitle();
            String author = book.getAuthor();
            double price = book.getPrice();
            int quantity = book.getQuantity();

            insertBook(title, author, price, quantity);
        }
    }


    private List<Book> parseBookCSV (String filename) {

        CsvMapper mapper = new CsvMapper();

        CsvSchema schema = CsvSchema.builder()
                .addColumn("title")
                .addColumn("author")
                .addColumn("price")
                .addColumn("quantity")
                .build()
                .withHeader();

        List<Book> bookList = new ArrayList<>();

        try (MappingIterator<Book> books = mapper.readerFor(Book.class)
                .with(schema)
                .readValues(new File(filename))) {

            return books.readAll();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
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


    @Override
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


    @Override
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
}