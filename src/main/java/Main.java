import dao.BookDao;
import dao.DatabaseUtil;
import dao.SqliteBookDao;
import service.BookService;

import java.util.Scanner;

public class Main {

    public static void initShell() {

        Scanner input = new Scanner(System.in);
        BookDao bookDao = new SqliteBookDao();
        BookService service = new BookService(input, bookDao);

        while (true) {
            System.out.println("원하시는 서비스를 선택하세요.");
            System.out.println("1. 도서 추가  |  2. 도서 조회  |  3. 도서 정보 수정  |  4. 도서 삭제  |  0. 종료 ");
            System.out.print("선 택 : ");
            int choice = input.nextInt();
            input.nextLine();

            if (choice == 0) {
                System.out.println("프로그램을 종료합니다.");
                input.close();
                return;
            } else if (choice == 1) {
                service.addBook();
            } else if (choice == 2) {
                service.searchBooks();
            } else if (choice == 3) {
                service.modifyBookInfo();
            } else if (choice == 4) {
                service.removeBook();
            } else {
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