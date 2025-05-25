package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static config.AppConfig.DB_URL;

public class DatabaseUtil {

    public static void initializeDatabase() {
        try {
            executeUpdate(SqliteBookDao.CREATE_BOOKS_TABLE);
            executeUpdate(SqliteLoanDao.CREATE_LOANS_TABLE);

        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 초기화 실패: " + e.getMessage());
        }
    }

    public static void executeUpdate(String sql) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        }
    }
}