package dao;

import model.Loan;

import java.util.List;

public class SqliteLoanDao implements LoanDao {

    @Override
    public boolean borrowBook(int bookId, String borrower) {

    }


    @Override
    public boolean returnBook(int loanId) {

    }


    @Override
    public List<Loan> findActiveLoans() {

    }


    @Override
    public List<Loan> findLoansByUser(String borrower) {

    }

}