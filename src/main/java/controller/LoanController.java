package controller;

import service.LoanService;

import java.util.Map;

public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    public String handleBorrowRequest(Map<String, String> params) {
        int bookId = Integer.parseInt(params.get("bookId"));
        String borrower = params.get("borrower");

        boolean success = loanService.borrowBook(bookId, borrower);
        return "";
    }
}
