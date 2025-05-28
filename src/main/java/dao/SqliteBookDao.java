package dao;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import log.AppLog;
import model.Book;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static config.AppConfig.DB_URL;

public class SqliteBookDao implements BookDao {

    static final String CREATE_BOOKS_TABLE = "CREATE TABLE IF NOT EXISTS books ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "title TEXT NOT NULL, "
            + "author TEXT, "
            + "price REAL, "
            + "quantity INTEGER"
            + ");";


    @Override
    public boolean insertBook(String title, String author, double price, int quantity) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String sql = "INSERT INTO books(title, author, price, quantity) " +
                    "VALUES(?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, title);
                pstmt.setString(2, author);
                pstmt.setDouble(3, price);
                pstmt.setInt(4, quantity);
                pstmt.executeUpdate();
            }

            String msg = String.format(
                    "도서 {제목: %s, 저자: %s}가 성공적으로 DB에 저장되었습니다.", title, author);
            AppLog.writeLog(msg);

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
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String sql = "DELETE FROM books WHERE id = ?";

            Book book = findById(id);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            String msg = String.format("도서 {ID: %s, 제목: %s, 저자: %s} 가 DB에서 삭제되었습니다.",
                    id, book.getTitle(), book.getAuthor());
            AppLog.writeLog(msg);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateBookPrice(int id, double price) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String sql = "UPDATE books SET price = ? WHERE id = ?";

            Book book = findById(id);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setDouble(1, price);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }

            String msg = String.format(
                    "도서 {ID: %d, 제목: %s, 저자: %s}의 가격이 %f에서 %f로 변경되었습니다",
                    id, book.getTitle(), book.getAuthor(), book.getPrice(), price);
            AppLog.writeLog(msg);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateQuantity(int id, int quantity) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String sql = "UPDATE books SET quantity = ? WHERE id = ?";

            Book book = findById(id);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, quantity);
                pstmt.setInt(2, id);
                pstmt.executeUpdate();
            }

            String msg = String.format(
                    "도서 {ID: %d, 제목: %s, 저자: %s}의 수량이 %d에서 %d로 변경되었습니다",
                    id, book.getTitle(), book.getAuthor(), book.getQuantity(), quantity);
            AppLog.writeLog(msg);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book findById(int id) {
        final String SELECT_BOOK_BY_ID = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BOOK_BY_ID)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Book book = new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    );
                    return book;
                }
            }
        } catch (SQLException e) {
            System.err.println("도서 검색 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<Book> findByTitleOrAuthor(String substring) {

        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String sql = "SELECT * FROM books" +
                    " WHERE title LIKE ? COLLATE NOCASE" +
                    " OR author LIKE ? COLLATE NOCASE;";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + substring + "%");
            pstmt.setString(2, "%" + substring + "%");
            ResultSet rs = pstmt.executeQuery();

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


    @Override
    public List<Book> findByPriceRange(double min, double max) {

        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String sql = "SELECT * FROM books WHERE price BETWEEN ? AND ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, min);
            pstmt.setDouble(2, max);
            ResultSet rs = pstmt.executeQuery();

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


    @Override
    public List<Book> findLowStock(int threshold) {

        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

            String sql = "SELECT * FROM books WHERE quantity <= ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, threshold);
            ResultSet rs = pstmt.executeQuery();

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


    @Override
    public List<Book> getAllBooks() {

        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {

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