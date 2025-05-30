package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
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

    public Loan(int id, int bookId, String borrower, String loanDate) {
        this.id = id;
        this.bookId = bookId;
        this.borrower = borrower;
        this.loanDate = loanDate;
    }
}