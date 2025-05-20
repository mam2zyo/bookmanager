package dao;

import model.Book;

import java.util.List;

public interface BookSearchDao {
    Book findById(int id);
    List<Book> findByTitleOrAuthor(String substring);
    List<Book> findByPriceRange(double min, double max);
    List<Book> findLowStock (int threshold);
    List<Book> getAllBooks();
}
