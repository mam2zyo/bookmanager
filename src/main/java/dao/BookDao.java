package dao;

import model.Book;
import java.util.List;

public interface BookDao {
    void createBookTable();
    List<Book> getAllBooks();
    void insertBook(String title, String author, double price, int quantity);
    void insertBook(String filename);
    void deleteBook(int id);
    void updateBookPrice(int id, double price);
    void updateQuantity(int id, int quantity);
}