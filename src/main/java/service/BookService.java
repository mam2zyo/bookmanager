package service;

import model.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BookService {
    private final List<Book> library = new ArrayList<>();

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book can't be a null");
        }
        library.add(book);
        System.out.println(book.getTitle() + "이 정상적으로 목록에 추가되었습니다.");
    }

    public Optional<Book> findByBookId(int id) {
        return library.stream()
                .filter(book -> book.getId() == id)
                .findFirst();
    }

    public List<Book> findByTitleOrAuthor(String substring) {
        if (substring == null || substring.isEmpty()) {
            throw new IllegalArgumentException("Invalid Argument");
        }
        return library.stream()
                .filter(book -> book.getTitle().contains(substring)
                        || book.getAuthor().contains(substring))
                .collect(Collectors.toList());
    }

    public void modifyBookInfo(String bookId) {

        if (findByBookId(bookId).isEmpty()) {
            System.out.println("도서가 없습니다.");
            return;
        }

        Book book = findByBookId(bookId).get();

        try (Scanner input = new Scanner(System.in)) {

            while (true) {

                System.out.println(book);
                System.out.println("1. 출판년도  |  2. 보유 수량   |  0. 종료");
                System.out.print("변경하려는 정보를 선택해주세요: ");

                int choice = input.nextInt();
                input.nextLine();

                if (choice == 0) {
                    System.out.println("도서 정보 수정을 종료합니다.");
                    break;
                } else if (choice == 1) {
                    System.out.print("출판년도: ");
                    int newPublicationYear = input.nextInt();
                    input.nextLine();
                    book.setPublicationYear(newPublicationYear);
                } else if (choice == 2) {
                    System.out.print("변경된 보유량: ");
                    int newBookStock = input.nextInt();
                    input.nextLine();
                    book.setBookStock(newBookStock);
                } else {
                    System.out.println("입력값이 정확하지 않습니다.");
                }
            }
        }
    }

    public void showBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("목록에 도서가 없습니다.");
        } else {
            books.forEach(System.out::println);
        }
    }

    public void showAllBooks() {
        showBooks(library);
    }

    public void removeBook(String bookId) {

        if (bookId == null || bookId.isEmpty()) {
            throw new IllegalArgumentException("Invalid argument");
        }

        Optional<Book> optionalBook = findByBookId(bookId);
        if (optionalBook.isEmpty()) {
            System.out.println("ID: " + bookId +"에 해당하는 도서가 없습니다.");
        } else {
            library.remove(optionalBook.get());
        }
    }
}