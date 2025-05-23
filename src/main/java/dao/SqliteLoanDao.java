package dao;

import model.Loan;

import java.util.List;

public class SqliteLoanDao implements LoanDao {

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