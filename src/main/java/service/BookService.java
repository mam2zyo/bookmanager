package service;

import dao.BookDao;
import model.Book;

import java.util.List;
import java.util.Scanner;

public class BookService {
    private final Scanner input;
    private final BookDao bookDao;

    public BookService(Scanner input, BookDao dao) {
        this.input = input;
        this.bookDao = dao;
    }

    public void createTable() {
        bookDao.createBookTable();
    }


    public void addBook() {

        System.out.println("입력방법을 선택하세요.");
        System.out.print("1. 직접 입력  | 2. csv 파일 입력 : ");

        int choice = input.nextInt();
        input.nextLine();

        if (choice == 1) {
            System.out.print("제목: ");
            String title = input.nextLine();
            System.out.print("저자: ");
            String author = input.nextLine();
            System.out.print("가격: ");
            double price = input.nextDouble();
            input.nextLine();
            System.out.print("수량: ");
            int quantity = input.nextInt();
            input.nextLine();

            bookDao.insertBook(title, author, price, quantity);
        } else if (choice == 2) {
            System.out.println("파일명을 경로와 함께 적어주세요: ");
            String filename = input.nextLine();

            bookDao.insertBooks(filename);
        }
    }


    public void modifyBookInfo() {

        System.out.println("변경하려는 도서의 id를 입력하세요");
        System.out.print("id : ");
        int id = input.nextInt();
        input.nextLine();

        if (bookDao.findById(id) == null) {
            System.out.println("해당 ID의 도서가 없습니다.");
            return;
        }

        Book book = bookDao.findById(id);

        System.out.println(book);
        System.out.println("1. 가격  |  2. 보유 수량");
        System.out.print("선택: ");

        int choice = input.nextInt();
        input.nextLine();

        if (choice == 1) {
            System.out.print("새로운 가격: ");
            double newPrice = input.nextDouble();
            input.nextLine();
            bookDao.updateBookPrice(id, newPrice);
        } else if (choice == 2) {
            System.out.print("현재 수량: ");
            int newBookStock = input.nextInt();
            input.nextLine();
            bookDao.updateQuantity(id, newBookStock);
        } else {
            System.out.println("입력값이 정확하지 않습니다.");
        }
    }


    public void removeBook() {

        System.out.println("삭제하려는 도서의 id를 입력하세요");
        System.out.print("id : ");
        int id = input.nextInt();
        input.nextLine();

        if (bookDao.findById(id) == null) {
            System.out.println("목록에 없는 도서입니다. 삭제할 수 없습니다.");
        } else {
            bookDao.deleteBook(id);
        }
    }


    public void searchBooks() {
        System.out.println("1. id  |  2. 제목 or 작가명  |  3. 가격  |  4. 재고  |  0. 전체 도서");
        System.out.print("조회 방법을 선택하세요: ");
        int choice = input.nextInt();
        input.nextLine();

        List<Book> books;

        switch (choice) {
            case 1:
                System.out.print("id를 입력하세요: ");
                int id = input.nextInt();
                input.nextLine();
                Book book = bookDao.findById(id);

                if (book == null) {
                    System.out.println("도서가 없습니다.");
                } else {
                    System.out.println(book);
                }
                break;

            case 2:
                System.out.print("도서명이나 작가명을 입력하세요: ");
                String str = input.nextLine();

                books = bookDao.findByTitleOrAuthor(str);

                String noMatchForSubstring = String.format("'" + str + "' 이(가) 포함된 도서가 없습니다.");

                showBooks(books, noMatchForSubstring);
                break;

            case 3:
                System.out.print("하한 가격: ");
                double min = input.nextDouble();
                input.nextLine();
                System.out.print("상한 가격: ");
                double max = input.nextDouble();
                input.nextLine();
                books = bookDao.findByPriceRange(min, max);

                String noMatchForPrice = String.format("가격이 %f 이상 %f 미만인 도서가 없습니다.",
                min, max);
                showBooks(books, noMatchForPrice);
                break;

            case 4:
                System.out.print("기준 수량: ");
                int threshold = input.nextInt();
                input.nextLine();
                books = bookDao.findLowStock(threshold);

                String noMatchForStock = String.format("재고 수량이 %d 이하인 도서가 없습니다.",
                        threshold);
                showBooks(books, noMatchForStock);
                break;

            case 0:
                books = bookDao.getAllBooks();
                String noBook = "등록된 도서가 없습니다.";
                showBooks(books, noBook);
                break;

            default:
                System.out.println("잘못된 번호입니다.");
        }
    }


    public void showBooks(List<Book> books, String msg) {
        if (books == null || books.isEmpty()) {
            System.out.println(msg);
        } else {
            books.forEach(System.out::println);
            System.out.println();
        }
    }
}