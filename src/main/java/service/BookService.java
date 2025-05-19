package service;

import dao.BookDao;
import model.Book;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BookService {
    private final Scanner input;
    private final BookDao dao;

    public BookService(Scanner input, BookDao dao) {
        this.input = input;
        this.dao = dao;
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

            dao.insertBook(filename);
        }
    }


    public Optional<Book> findByBookId(int id) {

        List<Book> library = dao.getAllBooks();

        return library.stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }


    public List<Book> findByTitleOrAuthor(String substring) {

        List<Book> library = dao.getAllBooks();

        if (substring == null || substring.isEmpty()) {
            throw new IllegalArgumentException("Invalid Argument");
        }
        return library.stream()
                .filter(book -> book.getTitle().contains(substring)
                        || book.getAuthor().contains(substring))
                .collect(Collectors.toList());
    }


    public void modifyBookInfo() {

        System.out.println("변경하려는 도서의 id를 입력하세요");
        System.out.print("id : ");
        int id = input.nextInt();
        input.nextLine();

        if (findByBookId(id).isEmpty()) {
            System.out.println("도서가 없습니다.");
            return;
        }

        Book book = findByBookId(id).get();

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


    public void showAllBooks() {

        List<Book> library = dao.getAllBooks();

        System.out.println("전체 도서 목록");
        showBooks(library);
        System.out.println();
    }


    public void removeBook() {

        System.out.println("삭제하려는 도서의 id를 입력하세요");
        System.out.print("id : ");
        int id = input.nextInt();
        input.nextLine();

        if (findByBookId(id).isEmpty()) {
            System.out.println("목록에 없는 도서입니다. 삭제할 수 없습니다.");
            return;
        } else {
            dao.deleteBook(id);
        }
    }
}