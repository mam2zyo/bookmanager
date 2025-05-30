package dao;

import model.Book;
import model.Loan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static config.AppConfig.DB_URL;

public class SqliteLoanDao implements LoanDao {

    private BookDao bookDao;

    static final String CREATE_LOANS_TABLE = "CREATE TABLE IF NOT EXISTS loans ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "book_id INTEGER NOT NULL, "
            + "borrower TEXT NOT NULL, "
            + "loan_date TEXT NOT NULL, "
            + "return_date TEXT, "
            + "FOREIGN KEY (book_id) REFERENCES books(id))";

    public SqliteLoanDao (BookDao bookDao) {
        this.bookDao = bookDao;
    }


    @Override
    public boolean borrowBook(int bookId, String borrower) {

        Book book = bookDao.findById(bookId);

        if (book == null) {
            System.out.println("ID: " + bookId + "인 도서가 없습니다.");
            return false;
        }

        int quantity = book.getQuantity();

        if (quantity < 1) {
            System.out.println("도서 재고가 없습니다.");
            return false;
        }

        final String INSERT_LOAN =
                "INSERT INTO loans(book_id, borrower, loan_date)" +
                        " VALUES(?, ?, date('now','localtime'))";

        final String DECREASE_BOOK_QUANTITY =
                "UPDATE books SET quantity = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_LOAN)) {

                pstmt.setInt(1, bookId);
                pstmt.setString(2, borrower);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("대출 등록 실패: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            try (PreparedStatement pstmt2 = conn.prepareStatement(DECREASE_BOOK_QUANTITY)) {

                pstmt2.setInt(1, quantity - 1);
                pstmt2.setInt(2, bookId);
                pstmt2.executeUpdate();

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("도서 수량 재설정 실패: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("DB 접속 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    private Loan findLoanById(int id) {

        final String SELECT_LOAN_BY_ID =
                "SELECT id, book_id, borrower, loan_date, return_date FROM loans WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(SELECT_LOAN_BY_ID)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Loan loan = new Loan(
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getString("borrower"),
                            rs.getString("loan_date"),
                            rs.getString("return_date")
                    );
                    return loan;
                }
            }
        } catch (SQLException e) {
            System.err.println("도서 대출 검색 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public boolean returnBook(int loanId) {

        Loan loan = findLoanById(loanId);

        if (loan == null) {
            System.out.println("해당하는 ID의 대출건이 확인되지 않습니다.");
            return false;
        }

        int bookId = loan.getBookId();
        Book book = bookDao.findById(bookId);
        int quantity = book.getQuantity();

        final String UPDATE_RETURN_DATE =
                "UPDATE loans SET return_date = date('now','localtime') WHERE id = ?";
        final String UPDATE_BOOK_QUANTITY =
                "UPDATE books SET quantity = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL)){

            conn.setAutoCommit(false);

            // 반납일 등록
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_RETURN_DATE)) {

                pstmt.setInt(1, loanId);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("반납일 추가 실패: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            // 도서 수량 증가
            try (PreparedStatement pstmt = conn.prepareStatement(UPDATE_BOOK_QUANTITY)) {

                pstmt.setInt(1, quantity + 1);
                pstmt.setInt(2, bookId);
                pstmt.executeUpdate();

            } catch (SQLException e) {
                conn.rollback();
                System.err.println("도서 수량 조정 실패: " + e.getMessage());
                e.printStackTrace();
                return false;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("DB 접속 오류: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public List<Loan> findActiveLoans() {

        List<Loan> loans = new ArrayList<>();

        final String FIND_ACTIVE_LOAN =
                "SELECT * FROM loans WHERE return_date IS NULL";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(FIND_ACTIVE_LOAN)) {

                while (rs.next()) {
                    Loan loan = new Loan(
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getString("borrower"),
                            rs.getString("return_date")
                    );
                    loans.add(loan);
                }
                return loans;

        } catch (Exception e) {
            System.err.println("도서 대출 검색 중 오류: " +  e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }


    @Override
    public List<Loan> findLoansByUser(String borrower) {

        List<Loan> loans = new ArrayList<>();

        final String FIND_LOANS_BY_USER =
                "SELECT * FROM loans WHERE borrower LIKE ? COLLATE NOCASE";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(FIND_LOANS_BY_USER)) {

            pstmt.setString(1, "%" + borrower + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = new Loan(
                            rs.getInt("id"),
                            rs.getInt("book_id"),
                            rs.getString("borrower"),
                            rs.getString("loan_date"),
                            rs.getString("return_date")
                    );
                    loans.add(loan);
                }
                return loans;
            }
        } catch (SQLException e) {
            System.err.println("도서 대출 검색 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}