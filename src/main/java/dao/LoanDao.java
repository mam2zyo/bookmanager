package dao;

import model.Loan;

import java.util.List;

public interface LoanDao {
    boolean borrowBook(int bookId, String borrower);
    boolean returnBook(int loanId);
    List<Loan> findActiveLoans();
    List<Loan> findLoansByUser(String borrower);
}