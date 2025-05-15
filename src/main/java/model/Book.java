package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Book {
    private final int id;
    private final String title;
    private final String author;
    @Setter
    private double price;
    @Setter
    private int quantity;

    public Book(int id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
        price = 0.0;
        quantity = 0;
    }
}