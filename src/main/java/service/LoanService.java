package service;

import dao.BookDao;
import dao.LoanDao;
import model.Book;
import model.Loan;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LoanService {
    private Scanner scanner;
    private LoanDao loanDao;
    private BookDao bookDao;

    public LoanService(Scanner scanner, LoanDao loanDao, BookDao bookDao) {
        this.scanner = scanner;
        this.loanDao = loanDao;
        this.bookDao = bookDao;
    }

    public boolean borrowBook() {

        System.out.print("대출하려는 도서의 ID를 입력하세요: ");
        int bookId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("대출하시는 분의 이름을 적어주세요: ");
        String borrower = scanner.nextLine();

        Book book = bookDao.findById(bookId);

        if (book == null) {
            System.out.println("도서가 존재하지 않습니다.");
            return false;
        }

        if (book.getQuantity() < 1) {
            System.out.println("재고가 부족합니다.");
            return false;
        }

        List<Loan> loans = loanDao.findLoansByUser(borrower);
        List<Integer> bookIds = new ArrayList<>();

        for (Loan loan : loans) {
            bookIds.add(loan.getBookId());
        }

        if (bookIds.contains(bookId)) {
            System.out.println("동일한 도서를 추가로 대출할 수 없습니다.");
            return false;
        }
        return loanDao.borrowBook(bookId, borrower);
    }


    public boolean returnBook() {
        System.out.print("반납하려는 대출 ID를 입력하세요: ");
        int loanId = scanner.nextInt();
        scanner.nextLine();

        return loanDao.returnBook(loanId);
    }


    public List<Loan> getActiveLoans() {
        return loanDao.findActiveLoans();
    }


    public void showActiveLoans() {

        List<Loan> loans = loanDao.findActiveLoans();

        if (loans.isEmpty()) {
            System.out.println("미반납 도서가 없습니다.");
            return;
        }

        System.out.println(" 번호 |           도서명         |       저자        |     대출자     |       대출일        ");
        for (int i = 0; i < loans.size(); i++) {
            Book book = bookDao.findById(loans.get(i).getBookId());
            System.out.printf("%d\t%s\t%s\t%s\t%s",
                    i + 1, book.getTitle(), book.getAuthor(), loans.get(i).getBorrower(), loans.get(i).getLoanDate());
            System.out.println();
        }
    }


    public List<Loan> getLoansByUser() {
        System.out.print("검색하려는 사용자 이름을 입력하세요: ");
        String borrower = scanner.nextLine();

        return loanDao.findLoansByUser(borrower);
    }

    public void showLoansByUser() {



    }
}