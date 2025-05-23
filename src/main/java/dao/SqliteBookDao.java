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
    private final String LOGFILE = "src/main/resources/books.log";

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


    private void logBook(String msg) {
        try (FileWriter fw = new FileWriter(LOGFILE, true);
             BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(msg);
            bw.newLine();

        } catch (IOException e) {
            System.err.println("Log File 작성 중 오류 발생 " +
                    e.getMessage());
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

            String msg = String.format(
                    "도서 {제목: %s, 저자: %s}가 성공적으로 DB에 저장되었습니다.",
                    title, author);
            logBook(msg);

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

            Book book = findById(id);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            String msg = String.format("도서 {ID: %s, 제목: %s, 저자: %s} 가 DB에서 삭제되었습니다.",
                    id, book.getTitle(), book.getAuthor());
            logBook(msg);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateBookPrice(int id, double price) {
        try (Connection conn = DriverManager.getConnection(URL)) {

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
            logBook(msg);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void updateQuantity(int id, int quantity) {
        try (Connection conn = DriverManager.getConnection(URL)) {

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
            logBook(msg);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Book findById(int id) {

        Book book = null;

        try (Connection conn = DriverManager.getConnection(URL)) {
            String sql = "SELECT * FROM books WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return book;
    }


    @Override
    public List<Book> findByTitleOrAuthor(String substring) {

        List<Book> books = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(URL)) {

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

        try (Connection conn = DriverManager.getConnection(URL)) {

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

        try (Connection conn = DriverManager.getConnection(URL)) {

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