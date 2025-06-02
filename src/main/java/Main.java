import dao.*;
import service.BookService;
import service.LoanService;

import java.util.Scanner;

public class Main {

    public static void initShell() {

        Scanner scanner = new Scanner(System.in);
        BookDao bookDao = new SqliteBookDao();
        LoanDao loanDao = new SqliteLoanDao(bookDao);
        BookService bookService = new BookService(scanner, bookDao);
        LoanService loanService = new LoanService(scanner, loanDao, bookDao);

        while (true) {

            System.out.println("원하시는 서비스를 선택하세요.");
            System.out.println("1. 도서 관리  |  2. 대출 관리  |  0. 종료 ");
            System.out.print("선 택 : ");

            int choiceMain = scanner.nextInt();
            scanner.nextLine();

            if (choiceMain == 0) {

                System.out.println("프로그램을 종료합니다.");
                scanner.close();
                return;

            } else if (choiceMain == 1) {

                System.out.println("1. 도서 추가  |  2. 도서 조회  |  3. 도서 정보 수정  |  4. 도서 삭제");
                System.out.print("선 택 : ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        bookService.addBook(); break;
                    case 2:
                        bookService.searchBooks(); break;
                    case 3:
                        bookService.modifyBookInfo(); break;
                    case 4:
                        bookService.removeBook(); break;
                    default:
                        System.out.println("잘못된 입력입니다.");
                }

            } else if (choiceMain == 2) {

                System.out.println("1. 대출 등록  |  2. 도서 반납  |  3. 대출 조회");
                System.out.print("선 택 : ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 :
                        loanService.borrowBook(); break;
                    case 2 :
                        loanService.returnBook(); break;
                    case 3:
                        loanService.searchLoans(); break;
                    default:
                        System.out.println("잘못된 입력입니다.");
                }

            }  else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    public static void main(String[] args) {

        try {
            DatabaseUtil.initializeDatabase();
        } catch (RuntimeException e) {
            System.err.println("DB 초기화 오류: " + e.getMessage());
            System.out.println("프로그램을 종료합니다.");
            return;
        }
        Main.initShell();
    }
}