package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @JsonIgnore
    private int id;
    private String title;
    private String author;
    private double price;
    private int quantity;

    public Book(String title, String author, double price, int quantity) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
    }
}