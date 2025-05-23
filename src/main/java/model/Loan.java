package model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Loan {
    private int id;
    @Setter
    private int bookId;
    private String borrower;
    private String loanDate;
    private String returnDate;

    public Loan(String borrower, String loanDate) {
        this.borrower = borrower;
        this.loanDate = loanDate;
    }
}