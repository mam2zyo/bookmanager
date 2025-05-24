package dao;

import java.sql.SQLException;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        try {
            DatabaseUtil.executeUpdate(SqliteBookDao.CREATE_BOOKS_TABLE);
            DatabaseUtil.executeUpdate(SqliteLoanDao.CREATE_LOANS_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException("데이터베이스 초기화 실패: " + e.getMessage());
        }
    }
}