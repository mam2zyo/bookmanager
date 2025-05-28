package dao;

import log.AppLog;
import model.Book;
import model.Loan;

import java.sql.*;
import java.util.List;

import static config.AppConfig.DB_URL;

public class SqliteLoanDao implements LoanDao {

    static final String CREATE_LOANS_TABLE = "CREATE TABLE IF NOT EXISTS loans ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "book_id INTEGER NOT NULL, "
            + "borrower TEXT NOT NULL, "
            + "loan_date TEXT NOT NULL, "
            + "return_date TEXT, "
            + "FOREIGN KEY (book_id) REFERENCES books(id));";

    private Book findById(int id) {
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

    private boolean isBorrowable(int bookId) {

        Book book = findById(bookId);

        if (book == null) {
            System.out.println("대여하려는 도서가 없습니다.");
            return false;
        } else if (book.getQuantity() < 1 ) {
            System.out.println("도서 재고가 없습니다.");
            return false;
        } else {
            System.out.println("대여 가능합니다.");
            return true;
        }
    }


    @Override
    public boolean borrowBook(int bookId, String borrower) {

        if (!isBorrowable(bookId)) {
            return false;
        }

        int quantity = findById(bookId).getQuantity();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            try {
                final String BORROW_BOOK =
                        "INSERT INTO loans(book_id, borrower, loan_date)" +
                                " VALUES(?, ?, date('now','localtime')";
                PreparedStatement pstmt = conn.prepareStatement(BORROW_BOOK);
                pstmt.setInt(1, bookId);
                pstmt.setString(2, borrower);
                pstmt.executeUpdate();

                final String DECREASE_BOOK_QUANTITY =
                        "UPDATE books SET quantity = ? WHERE id = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(DECREASE_BOOK_QUANTITY);
                pstmt2.setInt(1, quantity - 1);
                pstmt2.setInt(2, bookId);
                pstmt2.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("트랜잭션 실패: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("DB 접속 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean returnBook(int loanId) {
        return true;
    }


    @Override
    public List<Loan> findActiveLoans() {
        return null;
    }


    @Override
    public List<Loan> findLoansByUser(String borrower) {
        return null;
    }
}