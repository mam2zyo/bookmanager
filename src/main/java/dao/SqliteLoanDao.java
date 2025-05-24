package dao;

import model.Loan;

import java.util.List;

public class SqliteLoanDao implements LoanDao {

    static final String CREATE_LOANS_TABLE = "CREATE TABLE IF NOT EXISTS loans ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "book_id INTEGER NOT NULL, "
            + "borrower TEXT NOT NULL, "
            + "loan_date TEXT NOT NULL, "
            + "return_date TEXT, "
            + "FOREIGN KEY (book_id) REFERENCES books(id));";


    @Override
    public boolean borrowBook(int bookId, String borrower) {


        return true;
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