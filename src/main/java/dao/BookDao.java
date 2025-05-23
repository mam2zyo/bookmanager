package dao;

import model.Book;
import java.util.List;

public interface BookDao {
    void createBookTable();
    boolean insertBook(String title, String author, double price, int quantity);
    void insertBooks(String filename);
    void deleteBook(int id);
    void updateBookPrice(int id, double price);
    void updateQuantity(int id, int quantity);
    Book findById(int id);
    List<Book> findByTitleOrAuthor(String substring);
    List<Book> findByPriceRange(double min, double max);
    List<Book> findLowStock (int threshold);
    List<Book> getAllBooks();
}