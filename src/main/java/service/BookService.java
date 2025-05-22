package service;

import dao.BookDao;
import dao.BookSearchDao;
import model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BookService {
    private final Scanner input;
    private final BookDao dao;
    private final BookSearchDao searchDao;

    public BookService(Scanner input, BookDao dao, BookSearchDao searchDao) {
        this.input = input;
        this.dao = dao;
        this.searchDao = searchDao;
    }

    public void createTable() {
        dao.createBookTable();
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

            dao.insertBook(title, author, price, quantity);
        } else if (choice == 2) {
            System.out.println("파일명을 경로와 함께 적어주세요: ");
            String filename = input.nextLine();

            dao.insertBooks(filename);
        }
    }


    public void searchBooks() {
        System.out.println("1. id  |  2. 제목 or 작가명  |  3. 가격  |  4. 재고  |  0. 전체 도서");
        System.out.print("조회 방법을 선택하세요: ");
        int choice = input.nextInt();
        input.nextLine();

        List<Book> books = new ArrayList<>();

        switch (choice) {
            case 1:
                System.out.print("id를 입력하세요: ");
                int id = input.nextInt();
                input.nextLine();
                Book book = searchDao.findById(id);

                if (book == null) {
                    System.out.println("도서가 없습니다.");
                } else {
                    System.out.println(book);
                }
                break;

            case 2:
                System.out.print("도서명이나 작가명을 입력하세요: ");
                String str = input.nextLine();

                books = searchDao.findByTitleOrAuthor(str);

                if (books.isEmpty()) {
                    System.out.println("해당 문자열이 포함된 도서가 없습니다.");
                } else {
                    for (Book item : books) {
                        System.out.println(item);
                    }
                }
                break;

            case 3:
                System.out.print("하한 가격: ");
                double min = input.nextDouble();
                input.nextLine();
                System.out.print("상한 가격: ");
                double max = input.nextDouble();
                input.nextLine();
                books = searchDao.findByPriceRange(min, max);

                if (books.isEmpty()) {
                    System.out.println("해당 가격대의 도서가 없습니다.");
                } else {
                    for (Book item : books) {
                        System.out.println(item);
                    }
                }
                break;

            case 4:
                System.out.print("기준 수량: ");
                int threshold = input.nextInt();
                input.nextLine();
                books = searchDao.findLowStock(threshold);

                if (books.isEmpty()) {
                    System.out.println("기준 수량 이하인 책이 없습니다.");
                } else {
                    for (Book item : books) {
                        System.out.println(item);
                    }
                }
                break;

            case 0:
                books = searchDao.getAllBooks();

                if (books.isEmpty()) {
                    System.out.println("등록된 도서가 없습니다.");
                } else {
                    for (Book item : books) {
                        System.out.println(item);
                    }
                }
                break;

            default:
                System.out.println("잘못된 번호입니다.");
        }
    }


    public void modifyBookInfo() {

        System.out.println("변경하려는 도서의 id를 입력하세요");
        System.out.print("id : ");
        int id = input.nextInt();
        input.nextLine();

        if (searchDao.findById(id) == null) {
            System.out.println("도서가 없습니다.");
            return;
        }

        Book book = searchDao.findById(id);

        System.out.println(book);
        System.out.println("1. 가격  |  2. 보유 수량");
        System.out.print("선택: ");

        int choice = input.nextInt();
        input.nextLine();

        if (choice == 1) {
            System.out.print("새로운 가격: ");
            double newPrice = input.nextDouble();
            input.nextLine();
            dao.updateBookPrice(id, newPrice);
        } else if (choice == 2) {
            System.out.print("현재 수량: ");
            int newBookStock = input.nextInt();
            input.nextLine();
            dao.updateQuantity(id, newBookStock);
        } else {
            System.out.println("입력값이 정확하지 않습니다.");
        }
    }


    public void showBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("목록에 도서가 없습니다.");
        } else {
            books.forEach(System.out::println);
            System.out.println();
        }
    }


    public void removeBook() {

        System.out.println("삭제하려는 도서의 id를 입력하세요");
        System.out.print("id : ");
        int id = input.nextInt();
        input.nextLine();

        if (searchDao.findById(id) == null) {
            System.out.println("목록에 없는 도서입니다. 삭제할 수 없습니다.");
        } else {
            dao.deleteBook(id);
        }
    }
}