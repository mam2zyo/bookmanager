package dao;

import model.Book;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface BookDao {
    void createBookTable();
    List<Book> getAllBooks();
    void insertBook(Book book);
    void deleteBook(int id);
    void updateBookPrice(int id, double price);
    void updateQuantity(int id, int quantity);
}