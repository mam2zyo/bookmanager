package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    static final String LOGFILE = "src/main/resources/books.log";
    static final String DB_URL = "jdbc:sqlite:src/main/resources/book_manager.db";

    public static void executeUpdate(String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}